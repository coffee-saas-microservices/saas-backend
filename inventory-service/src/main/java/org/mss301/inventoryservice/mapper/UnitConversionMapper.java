package org.mss301.inventoryservice.mapper;

import org.mapstruct.*;
import org.mss301.inventoryservice.dto.request.UnitConversionRequest;
import org.mss301.inventoryservice.dto.response.UnitConversionResponse;
import org.mss301.inventoryservice.entity.UnitConversion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UnitConversionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "inventoryStatus", ignore = true)
    UnitConversion toEntity(UnitConversionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    void updateFromRequest(@MappingTarget UnitConversion entity, UnitConversionRequest request);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "ingredientName", source = "ingredient.name")
    UnitConversionResponse toResponse(UnitConversion entity);
}
