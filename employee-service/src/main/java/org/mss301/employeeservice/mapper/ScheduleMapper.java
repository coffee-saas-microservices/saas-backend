package org.mss301.employeeservice.mapper;

import org.mapstruct.*;
import org.mss301.employeeservice.dto.request.ScheduleRequest;
import org.mss301.employeeservice.dto.response.ScheduleResponse;
import org.mss301.employeeservice.entity.Schedule;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ScheduleMapper {

    @Mapping(target = "employeeId", source = "employee.employeeId")
    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "employeeType", source = "employee.employeeType")
    ScheduleResponse toResponse(Schedule entity);

    @Mapping(target = "scheduleId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Schedule toEntity(ScheduleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "scheduleId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromRequest(ScheduleRequest request, @MappingTarget Schedule entity);
}
