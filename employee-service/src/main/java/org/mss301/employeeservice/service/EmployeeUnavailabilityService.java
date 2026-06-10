package org.mss301.employeeservice.service;

import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityFilter;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityRequest;
import org.mss301.employeeservice.dto.response.EmployeeUnavailabilityResponse;

public interface EmployeeUnavailabilityService {
    EmployeeUnavailabilityResponse create(EmployeeUnavailabilityRequest request);
    EmployeeUnavailabilityResponse update(Long id, EmployeeUnavailabilityRequest request);
    EmployeeUnavailabilityResponse getDetail(Long id);
    void delete(Long id);
    PageResponse<EmployeeUnavailabilityResponse> getAll(EmployeeUnavailabilityFilter filter);
}
