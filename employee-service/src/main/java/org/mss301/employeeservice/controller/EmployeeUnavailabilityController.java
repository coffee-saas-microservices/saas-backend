package org.mss301.employeeservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityFilter;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityRequest;
import org.mss301.employeeservice.dto.response.EmployeeUnavailabilityResponse;
import org.mss301.employeeservice.service.EmployeeUnavailabilityService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/unavailability")
@RequiredArgsConstructor
public class EmployeeUnavailabilityController {

    private final EmployeeUnavailabilityService unavailabilityService;

    @PostMapping
    public ResponseEntity<EmployeeUnavailabilityResponse> create(
            @Valid @RequestBody EmployeeUnavailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unavailabilityService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeUnavailabilityResponse>> getAll(
            @ParameterObject @ModelAttribute EmployeeUnavailabilityFilter filter) {
        return ResponseEntity.ok(unavailabilityService.getAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeUnavailabilityResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(unavailabilityService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeUnavailabilityResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUnavailabilityRequest request) {
        return ResponseEntity.ok(unavailabilityService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        unavailabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
