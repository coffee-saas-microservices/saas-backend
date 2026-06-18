package org.mss301.inventoryservice.service.impl;

import com.thoughtworks.xstream.core.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.commonservice.multitenancy.TenantFilter;
import org.mss301.inventoryservice.dto.filter.RawIngredientFilter;
import org.mss301.inventoryservice.dto.request.RawIngredientRequest;
import org.mss301.inventoryservice.dto.response.RawIngredientResponse;
import org.mss301.inventoryservice.entity.RawIngredient;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.mapper.RawIngredientMapper;
import org.mss301.inventoryservice.repository.IngredientBatchRepository;
import org.mss301.inventoryservice.repository.RawIngredientRepository;
import org.mss301.inventoryservice.service.inter.RawIngredientService;
import org.mss301.inventoryservice.specification.RawIngredientSpec;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RawIngredientServiceImpl implements RawIngredientService {

    private final RawIngredientRepository rawIngredientRepository;
    private final IngredientBatchRepository ingredientBatchRepository;
    private final RawIngredientMapper rawIngredientMapper;


    @Override
    @Transactional
    public RawIngredientResponse create(RawIngredientRequest request) {

        RawIngredient entity = rawIngredientMapper.toEntity(request);
        entity.setShopId(TenantContext.getCurrentShopId());
        entity.setInventoryStatus(InventoryStatus.ACTIVE);

        RawIngredient result = rawIngredientRepository.save(entity);

        return toFullResponse(result);
    }


    @Override
    @Transactional
    public RawIngredientResponse update(Long id, RawIngredientRequest request) {

        RawIngredient entity = rawIngredientRepository.findByIdAndShopId(id, TenantContext.getCurrentShopId())
                .orElseThrow(() -> new BusinessException("Nguyên liệu không tồn tại"));

        rawIngredientMapper.updateFromRequest(entity, request);

        RawIngredient result = rawIngredientRepository.save(entity);

        return toFullResponse(result);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<RawIngredientResponse> getAll(RawIngredientFilter filter) {
        return rawIngredientRepository.findAll(
                RawIngredientSpec.filter(filter, TenantContext.getCurrentShopId()),
                filter.toPageable()
        ).map(this::toFullResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public RawIngredientResponse getDetail(Long id) {
        RawIngredient entity = rawIngredientRepository.findByIdAndShopId(id, TenantContext.getCurrentShopId())
                .orElseThrow(() -> new BusinessException("Nguyên liệu không tồn tại"));
        return toFullResponse(entity);
    }


    private RawIngredientResponse toFullResponse(RawIngredient entity) {
        var res = rawIngredientMapper.toResponse(entity);
        // Tính tổng tồn kho từ Batch (Logic tính toán không thuộc về Mapper)
        Double totalStock = ingredientBatchRepository.sumQuantityByIngredientIdAndStatus(entity.getId(), InventoryStatus.ACTIVE);
        res.setTotalStockQuantity(totalStock != null ? totalStock : 0.0);
        return res;
    }
}
