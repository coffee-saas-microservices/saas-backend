package org.mss301.inventoryservice.mapper;

import org.mapstruct.*;
import org.mss301.inventoryservice.dto.request.StockCheckItemRequest;
import org.mss301.inventoryservice.dto.request.StockCheckStartRequest;
import org.mss301.inventoryservice.dto.response.StockCheckDetailResponse;
import org.mss301.inventoryservice.dto.response.StockCheckSessionResponse;
import org.mss301.inventoryservice.entity.StockCheckDetail;
import org.mss301.inventoryservice.entity.StockCheckSession;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockCheckMapper {

    @Mapping(target = "createdByName", ignore = true)
    StockCheckSessionResponse toSessionResponse(StockCheckSession entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "isApproved", constant = "false")
    @Mapping(target = "inventoryStatus", constant = "ACTIVE") // Hoặc DRAFT
    StockCheckSession toSessionEntity(StockCheckStartRequest request);

    @Mapping(source = "ingredient.id", target = "ingredientId")
    @Mapping(source = "ingredient.name", target = "ingredientName")
    StockCheckDetailResponse toDetailResponse(StockCheckDetail entity);

    List<StockCheckDetailResponse> toDetailResponseList(List<StockCheckDetail> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "snapshotQuantity", ignore = true)
    @Mapping(target = "diffQuantity", ignore = true)
    void updateDetailFromRequest(@MappingTarget StockCheckDetail entity, StockCheckItemRequest request);
}
