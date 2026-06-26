package org.mss301.inventoryservice.mapper;

import org.mapstruct.*;
import org.mss301.inventoryservice.dto.request.RawIngredientRequest;
import org.mss301.inventoryservice.dto.response.RawIngredientResponse;
import org.mss301.inventoryservice.entity.RawIngredient;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RawIngredientMapper {

    @Mapping(target = "totalStockQuantity", ignore = true)
    RawIngredientResponse toResponse(RawIngredient entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "inventoryStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RawIngredient toEntity(RawIngredientRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    void updateFromRequest(@MappingTarget RawIngredient entity, RawIngredientRequest request);
}
