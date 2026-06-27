package org.mss301.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.inventoryservice.client.IdentityServiceClient;
import org.mss301.inventoryservice.config.SecurityUtils;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.inventoryservice.dto.filter.StockCheckSessionFilter;
import org.mss301.inventoryservice.dto.request.StockCheckApproveRequest;
import org.mss301.inventoryservice.dto.request.StockCheckItemRequest;
import org.mss301.inventoryservice.dto.request.StockCheckStartRequest;
import org.mss301.inventoryservice.dto.request.StockCheckUpdateRequest;
import org.mss301.inventoryservice.dto.response.StockCheckSessionResponse;
import org.mss301.inventoryservice.entity.*;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.entity.enumeration.TransactionType;
import org.mss301.inventoryservice.mapper.StockCheckMapper;
import org.mss301.inventoryservice.repository.*;
import org.mss301.inventoryservice.service.inter.StockCheckService;
import org.mss301.inventoryservice.specification.StockCheckSessionSpec;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockCheckServiceImpl implements StockCheckService {

    private final StockCheckSessionRepository sessionRepository;
    private final StockCheckDetailRepository detailRepository;
    private final IngredientBatchRepository batchRepository;
    private final RawIngredientRepository ingredientRepository;
    private final StockCheckMapper stockCheckMapper;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final IdentityServiceClient identityServiceClient;


    @Override
    @Transactional
    public StockCheckSessionResponse startSession(StockCheckStartRequest request) {
        var shopId = TenantContext.getCurrentShopId();

        String keycloakUserId = SecurityUtils.getCurrentKeycloakUserId();
        if (keycloakUserId == null) {
            throw new BusinessException("Người dùng chưa được xác thực");
        }

        var userResponse = identityServiceClient.getUserByKeycloakId(keycloakUserId, SecurityUtils.getCurrentDomain()).getBody();
        if (userResponse == null || userResponse.getCustomerId() == null) {
            throw new BusinessException("Không tìm thấy thông tin tài khoản người dùng");
        }

        // 1. Tạo Session dùng Mapper
        StockCheckSession session = stockCheckMapper.toSessionEntity(request);
        session.setShopId(shopId);
        session.setCreatedBy(userResponse.getCustomerId());
        session.setInventoryStatus(InventoryStatus.ACTIVE);
        session.setCompletedAt(LocalDateTime.now()); // temporary, sẽ update sau khi approve

        session = sessionRepository.save(session);

        // 2. Xác định danh sách nguyên liệu cần kiểm
        List<RawIngredient> ingredients;
        if (request.getIngredientIds() == null || request.getIngredientIds().isEmpty()) {
            ingredients = ingredientRepository.findAllByShopId(shopId);
        } else {
            ingredients = ingredientRepository.findAllById(request.getIngredientIds());
        }

        // 3. Snapshot tồn kho
        for (RawIngredient ing : ingredients) {
            Double systemStock = batchRepository.sumQuantityByIngredientIdAndStatus(ing.getId(), InventoryStatus.ACTIVE);
            if (systemStock == null) systemStock = 0.0;

            StockCheckDetail detail = new StockCheckDetail();
            detail.setSession(session);
            detail.setIngredient(ing);
            detail.setSnapshotQuantity(systemStock);
            detail.setActualQuantity(0.0);
            detail.setDiffQuantity(0.0);
            detail.setInventoryStatus(InventoryStatus.ACTIVE);

            detailRepository.save(detail);
        }

        return getFullResponse(session);
    }


    @Override
    @Transactional
    public StockCheckSessionResponse updateCount(StockCheckUpdateRequest request) {

        StockCheckSession session = sessionRepository.findByIdAndShopId(request.getSessionId(), TenantContext.getCurrentShopId())
                .orElseThrow(() -> new BusinessException("Phiếu kiểm kê không tồn tại"));

        if (Boolean.TRUE.equals(session.getIsApproved())) {
            throw new BusinessException("Phiếu đã duyệt, không thể sửa");
        }

        for (StockCheckItemRequest itemReq : request.getDetails()) {
            StockCheckDetail detail = detailRepository.findBySessionIdAndIngredientId(session.getId(), itemReq.getIngredientId())
                    .orElseThrow(() -> new BusinessException("Nguyên liệu ID " + itemReq.getIngredientId() + " không có trong phiếu này"));

            // 1. Dùng Mapper để update (actualQuantity, reason)
            stockCheckMapper.updateDetailFromRequest(detail, itemReq);

            // 2. Tính toán diff
            if (detail.getActualQuantity() != null) {
                detail.setDiffQuantity(detail.getActualQuantity() - detail.getSnapshotQuantity());
            }

            detailRepository.save(detail);
        }

        return getFullResponse(session);
    }


    @Override
    @Transactional
    public StockCheckSessionResponse approveSession(StockCheckApproveRequest request) {

        StockCheckSession session = sessionRepository.findByIdAndShopId(request.getSessionId(), TenantContext.getCurrentShopId())
                .orElseThrow(() -> new BusinessException("Phiếu không tồn tại"));

        if (Boolean.TRUE.equals(request.getIsApproved())) {
            session.setIsApproved(true);

            String keycloakUserId = SecurityUtils.getCurrentKeycloakUserId();
            if (keycloakUserId != null) {
                var userResponse = identityServiceClient.getUserByKeycloakId(keycloakUserId, SecurityUtils.getCurrentDomain()).getBody();
                if (userResponse != null && userResponse.getCustomerId() != null) {
                    session.setApprovedBy(userResponse.getCustomerId());
                }
            } else {
                session.setApprovedBy(null);
            }

            session.setCompletedAt(LocalDateTime.now());
            session.setNote(request.getNote());
            session.setInventoryStatus(InventoryStatus.ACTIVE);

            List<StockCheckDetail> details = detailRepository.findAllBySessionId(session.getId());

            for (StockCheckDetail detail : details) {
                Double diff = detail.getDiffQuantity();

                // Bỏ qua nếu không có sự chênh lệch (Thực tế == Hệ thống)
                if (diff == null || diff == 0.0) {
                    continue;
                }

                RawIngredient ingredient = detail.getIngredient();

                if (diff < 0) {
                    // 1. HAO HỤT (THIẾU HÀNG): Trừ kho theo phương pháp FIFO
                    double amountToDeduct = Math.abs(diff);
                    List<IngredientBatch> activeBatches = batchRepository
                            .findByRawIngredientIdAndInventoryStatusOrderByExpiredAtAsc(ingredient.getId(), InventoryStatus.ACTIVE);

                    for (IngredientBatch batch : activeBatches) {
                        if (amountToDeduct <= 0) break; // Đã trừ đủ số lượng hụt

                        double deductAmount = Math.min(batch.getCurrentQuantity(), amountToDeduct);
                        if (deductAmount > 0) {
                            batch.setCurrentQuantity(batch.getCurrentQuantity() - deductAmount);
                            amountToDeduct -= deductAmount;
                            batchRepository.save(batch);

                            // Ghi lại lịch sử trừ kho
                            createAdjustmentTransaction(session.getShopId(), ingredient, batch, detail,
                                    -deductAmount, batch.getCurrentQuantity());
                        }
                    }

                    if (amountToDeduct > 0) {
                        // Cảnh báo: Số lượng hụt lớn hơn cả tổng tồn kho hiện tại (Có thể do sai sót hệ thống nghiêm trọng)
                        // Trong thực tế có thể throw exception hoặc cho phép kho âm. Ở đây ta tạm thời ném lỗi để kiểm soát.
                        throw new BusinessException("Lỗi: Số lượng hụt của " + ingredient.getName() + " lớn hơn tổng tồn các lô!");
                    }

                } else {
                    // 2. DƯ THỪA (THỪA HÀNG): Cộng dồn vào lô có hạn sử dụng dài nhất
                    List<IngredientBatch> activeBatches = batchRepository
                            .findByRawIngredientIdAndInventoryStatusOrderByExpiredAtDesc(ingredient.getId(), InventoryStatus.ACTIVE);

                    if (!activeBatches.isEmpty()) {
                        IngredientBatch latestBatch = activeBatches.get(0);
                        latestBatch.setCurrentQuantity(latestBatch.getCurrentQuantity() + diff);
                        batchRepository.save(latestBatch);

                        // Ghi lại lịch sử cộng kho
                        createAdjustmentTransaction(session.getShopId(), ingredient, latestBatch, detail,
                                diff, latestBatch.getCurrentQuantity());
                    } else {
                        // Nếu quán hoàn toàn chưa nhập hàng (không có lô) mà lại kiểm ra thừa hàng
                        throw new BusinessException("Lỗi: Phát hiện dư thừa " + ingredient.getName() + " nhưng nguyên liệu này chưa từng được nhập lô nào. Vui lòng tạo phiếu Nhập Kho trước!");
                    }
                }
            }
            // ==============================================================
            // KẾT THÚC LOGIC CÂN BẰNG KHO
            // ==============================================================

        } else {
            session.setInventoryStatus(InventoryStatus.INACTIVE); // Hủy phiếu
        }

        StockCheckSession result = sessionRepository.save(session);

        return getFullResponse(result);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<StockCheckSessionResponse> getAll(StockCheckSessionFilter filter) {
        return sessionRepository.findAll(
                StockCheckSessionSpec.filter(filter, TenantContext.getCurrentShopId()),
                filter.toPageable()
        ).map(this::getFullResponse);
    }


    private StockCheckSessionResponse getFullResponse(StockCheckSession session) {
        var response = stockCheckMapper.toSessionResponse(session);
        // Map list details
        response.setDetails(stockCheckMapper.toDetailResponseList(detailRepository.findAllBySessionId(session.getId())));
        if (session.getCreatedBy() != null) {
            try {
                var userResponse = identityServiceClient.getUserById(session.getCreatedBy(), SecurityUtils.getCurrentDomain()).getBody();
                if (userResponse != null) {
                    response.setCreatedByName(userResponse.getFullname());
                }
            } catch (Exception e) {
                log.error("Lỗi khi lấy thông tin người tạo phiếu kiểm kho: {}", session.getCreatedBy(), e);
                response.setCreatedByName("Unknown User");
            }
        }
        return response;
    }


    private void createAdjustmentTransaction(Long shopId, RawIngredient ingredient, IngredientBatch batch,
                                             StockCheckDetail detail, Double quantityChange, Double quantityAfter) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setShopId(shopId);
        transaction.setIngredient(ingredient);
        transaction.setBatch(batch);
        transaction.setStockCheckDetail(detail);
        transaction.setQuantityChange(quantityChange);
        transaction.setQuantityAfter(quantityAfter);
        transaction.setTransactionType(TransactionType.STOCK_CHECK_ADJUST);
        transaction.setInventoryStatus(InventoryStatus.ACTIVE);

        inventoryTransactionRepository.save(transaction);
    }
}
