package org.mss301.employeeservice.mapper;

import org.mapstruct.*;
import org.mss301.employeeservice.dto.request.ShiftTemplateRequest;
import org.mss301.employeeservice.dto.response.ShiftTemplateResponse;
import org.mss301.employeeservice.entity.ShiftTemplate;

import java.time.LocalTime;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShiftTemplateMapper {

    ShiftTemplateResponse toResponse(ShiftTemplate shiftTemplate);

    @Mapping(target = "shiftTemplateId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShiftTemplate toEntity(ShiftTemplateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shiftTemplateId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(ShiftTemplateRequest request, @MappingTarget ShiftTemplate shiftTemplate);

    /** MapStruct tự động dùng method này khi map String → LocalTime */
    default LocalTime toLocalTime(String time) {
        return (time != null && !time.isBlank()) ? LocalTime.parse(time) : null;
    }
}
