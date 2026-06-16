package org.mss301.subscriptionservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mss301.subscriptionservice.dto.request.SubscriptionPlanRequest;
import org.mss301.subscriptionservice.dto.response.SubscriptionPlanResponse;
import org.mss301.subscriptionservice.entity.SubscriptionPlan;

import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionPlanMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "subscriptionPlanId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "configLimit", source = "configLimit", qualifiedByName = "mapToJsonString")
    SubscriptionPlan toEntity(SubscriptionPlanRequest request);

    @Mapping(target = "configLimit", source = "configLimit", qualifiedByName = "jsonStringToMap")
    SubscriptionPlanResponse toResponse(SubscriptionPlan entity);

    @Mapping(target = "subscriptionPlanId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "configLimit", source = "configLimit", qualifiedByName = "mapToJsonString")
    void updateEntityFromRequest(SubscriptionPlanRequest request, @MappingTarget SubscriptionPlan entity);

    // Chuyển đổi Map tính năng thành chuỗi JSON để lưu vào DB (jsonb)
    @Named("mapToJsonString")
    default String mapToJsonString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    // Chuyển đổi chuỗi JSON từ DB thành Map để Frontend dễ dàng hiển thị tính năng
    @Named("jsonStringToMap")
    default Map<String, Object> jsonStringToMap(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) return null;
        try {
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
