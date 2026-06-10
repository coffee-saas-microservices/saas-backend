package org.mss301.employeeservice.service;

import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.ShiftTemplateFilter;
import org.mss301.employeeservice.dto.request.ShiftTemplateRequest;
import org.mss301.employeeservice.dto.response.ShiftTemplateResponse;

public interface ShiftTemplateService {
    ShiftTemplateResponse create(ShiftTemplateRequest request);
    ShiftTemplateResponse update(Long id, ShiftTemplateRequest request);
    ShiftTemplateResponse getById(Long id);
    void delete(Long id);
    PageResponse<ShiftTemplateResponse> getAll(ShiftTemplateFilter filter);
}
