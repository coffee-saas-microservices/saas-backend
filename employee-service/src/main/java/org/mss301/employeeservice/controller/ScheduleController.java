package org.mss301.employeeservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.ScheduleFilter;
import org.mss301.employeeservice.dto.request.ScheduleRequest;
import org.mss301.employeeservice.dto.response.ScheduleResponse;
import org.mss301.employeeservice.service.ScheduleService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponse> create(
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ScheduleResponse>> getAll(
            @ParameterObject @ModelAttribute ScheduleFilter filter) {
        return ResponseEntity.ok(scheduleService.getAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getDetail(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ScheduleResponse>> getByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(scheduleService.getByEmployeeId(employeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
