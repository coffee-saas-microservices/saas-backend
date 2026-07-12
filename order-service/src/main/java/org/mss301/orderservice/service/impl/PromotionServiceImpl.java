package org.mss301.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.orderservice.dto.request.PromotionRequest;
import org.mss301.orderservice.dto.response.PromotionResponse;
import org.mss301.orderservice.entity.Promotion;
import org.mss301.orderservice.entity.enumeration.PromotionStatus;
import org.mss301.orderservice.mapper.PromotionMapper;
import org.mss301.orderservice.repository.PromotionRepository;
import org.mss301.orderservice.service.inter.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("ShopId không tìm thấy trong context");
        }

        if (promotionRepository.existsByPromotionCodeAndShopId(request.getPromotionCode(), shopId)) {
            throw new BusinessException("Mã khuyến mãi '" + request.getPromotionCode() + "' đã tồn tại trong cửa hàng");
        }

        Promotion promotion = promotionMapper.toEntity(request);
        promotion.setShopId(shopId);
        promotion.setStatus(PromotionStatus.INACTIVE);
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionResponse> getAllPromotions(BaseFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("ShopId không tìm thấy trong context");
        }
        return promotionRepository.findAllByShopId(shopId, filter.toPageable())
                .map(promotionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = findByIdOrThrow(id);
        return promotionMapper.toResponse(promotion);
    }

    @Override
    @Transactional
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = findByIdOrThrow(id);

        if (!promotion.getPromotionCode().equals(request.getPromotionCode())) {
            if (promotionRepository.existsByPromotionCodeAndShopId(request.getPromotionCode(), promotion.getShopId())) {
                throw new BusinessException("Mã khuyến mãi '" + request.getPromotionCode() + "' đã tồn tại trong cửa hàng");
            }
        }

        promotionMapper.updateEntity(request, promotion);
        promotion.setUpdatedAt(LocalDateTime.now());

        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PromotionResponse changeStatus(Long id, PromotionStatus status) {
        Promotion promotion = findByIdOrThrow(id);
        promotion.setStatus(status);
        promotion.setUpdatedAt(LocalDateTime.now());
        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = findByIdOrThrow(id);
        promotion.setStatus(PromotionStatus.DELETED);
        promotionRepository.save(promotion);
    }

    private Promotion findByIdOrThrow(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Khuyến mãi không tồn tại: " + id));
    }
}
