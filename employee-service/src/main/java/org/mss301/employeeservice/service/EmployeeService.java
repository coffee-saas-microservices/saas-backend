package org.mss301.employeeservice.service;

import org.mss301.employeeservice.dto.request.EmployeeRequest;
import org.mss301.employeeservice.dto.response.EmployeeResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(Long id);
}
