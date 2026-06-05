package org.mss301.employeeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mss301.employeeservice.dto.request.EmployeeRequest;
import org.mss301.employeeservice.dto.request.UpdateEmployeeRequest;
import org.mss301.employeeservice.dto.response.EmployeeResponse;
import org.mss301.employeeservice.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "shopId", source = "shopId")
    Employee toEntity(EmployeeRequest request, Long shopId);

    EmployeeResponse toResponse(Employee employee);

    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateEmployeeRequest request, @MappingTarget Employee employee);
}
