package org.mss301.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mss301.orderservice.dto.request.PromotionRequest;
import org.mss301.orderservice.dto.response.PromotionResponse;
import org.mss301.orderservice.entity.Promotion;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "status", source = "status")
    PromotionResponse toResponse(Promotion promotion);

    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Promotion toEntity(PromotionRequest request);

    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PromotionRequest request, @MappingTarget Promotion promotion);
}