package org.mss301.employeeservice.mapper;

import org.mapstruct.*;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityRequest;
import org.mss301.employeeservice.dto.response.EmployeeUnavailabilityResponse;
import org.mss301.employeeservice.entity.EmployeeUnavailability;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmployeeUnavailabilityMapper {

    @Mapping(target = "employeeId", source = "employee.employeeId")
    @Mapping(target = "employeeName", ignore = true)
    EmployeeUnavailabilityResponse toResponse(EmployeeUnavailability entity);

    @Mapping(target = "employeeUnavailabilityId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    EmployeeUnavailability toEntity(EmployeeUnavailabilityRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "employeeUnavailabilityId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromRequest(EmployeeUnavailabilityRequest request, @MappingTarget EmployeeUnavailability entity);
}
