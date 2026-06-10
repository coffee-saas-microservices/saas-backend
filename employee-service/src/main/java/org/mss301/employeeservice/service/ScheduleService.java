package org.mss301.employeeservice.service;

import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.ScheduleFilter;
import org.mss301.employeeservice.dto.request.ScheduleRequest;
import org.mss301.employeeservice.dto.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse create(ScheduleRequest request);

    ScheduleResponse update(Long id, ScheduleRequest request);

    ScheduleResponse getDetail(Long id);

    void delete(Long id);

    PageResponse<ScheduleResponse> getAll(ScheduleFilter filter);

    List<ScheduleResponse> getByEmployeeId(Long employeeId);
}
