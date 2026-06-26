package org.mss301.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.inventoryservice.dto.request.UnitConversionRequest;
import org.mss301.inventoryservice.dto.response.UnitConversionResponse;
import org.mss301.inventoryservice.entity.UnitConversion;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.mapper.UnitConversionMapper;
import org.mss301.inventoryservice.repository.RawIngredientRepository;
import org.mss301.inventoryservice.repository.UnitConversionRepository;
import org.mss301.inventoryservice.service.inter.UnitConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnitConversionServiceImpl implements UnitConversionService {

    private final UnitConversionRepository unitConversionRepository;
    private final RawIngredientRepository ingredientRepository;
    private final UnitConversionMapper unitConversionMapper;

    @Override
    @Transactional
    public UnitConversionResponse create(UnitConversionRequest request) {
        Long shopId = TenantContext.getCurrentShopId();

        var ingredient = ingredientRepository
                .findByIdAndShopId(request.getIngredientId(), shopId)
                .orElseThrow(() -> new BusinessException("Nguyên liệu không tồn tại"));

        if (unitConversionRepository.existsByIngredientIdAndFromUnitAndInventoryStatus(ingredient.getId(),
                request.getFromUnit(),
                InventoryStatus.ACTIVE)) {
            throw new BusinessException("Đơn vị " + request.getFromUnit() + " đã được cấu hình cho nguyên liệu này");
        }

        UnitConversion entity = unitConversionMapper.toEntity(request);
        entity.setShopId(TenantContext.getCurrentShopId());
        entity.setIngredient(ingredient);
        entity.setInventoryStatus(InventoryStatus.ACTIVE);

        entity = unitConversionRepository.save(entity);

        return unitConversionMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public UnitConversion update(Long id, UnitConversionRequest request) {

        UnitConversion entity = unitConversionRepository.findByIdAndShopId(id, TenantContext.getCurrentShopId())
                .orElseThrow(() -> new BusinessException("Cấu hình quy đổi không tồn tại"));

        unitConversionMapper.updateFromRequest(entity, request);

        UnitConversion result = unitConversionRepository.save(entity);

        return unitConversionRepository.save(result);
    }

    @Override
    public Double convertToBaseUnit(Long ingredientId, InputUnit fromUnit, Double quantity) {
        return unitConversionRepository
                .findByIngredientIdAndFromUnitAndInventoryStatus(ingredientId, fromUnit, InventoryStatus.ACTIVE)
                .map(conversion -> quantity * conversion.getConversionFactor())
                .orElseThrow(() -> new BusinessException("Chưa cấu hình quy đổi cho đơn vị: " + fromUnit));
    }
}
