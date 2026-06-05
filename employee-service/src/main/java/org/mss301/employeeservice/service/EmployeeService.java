package org.mss301.employeeservice.service;

import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.EmployeeFilter;
import org.mss301.employeeservice.dto.request.EmployeeRequest;
import org.mss301.employeeservice.dto.request.UpdateEmployeeRequest;
import org.mss301.employeeservice.dto.response.EmployeeResponse;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    PageResponse<EmployeeResponse> getAllEmployees(EmployeeFilter filter);
    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);
    void deleteEmployee(Long id);
}

