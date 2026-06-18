package org.mss301.inventoryservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mss301.inventoryservice.dto.response.IngredientBatchResponse;
import org.mss301.inventoryservice.entity.IngredientBatch;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IngredientBatchMapper {

    @Mapping(source = "rawIngredient.name", target = "ingredientName")
    IngredientBatchResponse toResponse(IngredientBatch entity);
}
