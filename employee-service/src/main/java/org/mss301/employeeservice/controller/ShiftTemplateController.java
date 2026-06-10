package org.mss301.employeeservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.ShiftTemplateFilter;
import org.mss301.employeeservice.dto.request.ShiftTemplateRequest;
import org.mss301.employeeservice.dto.response.ShiftTemplateResponse;
import org.mss301.employeeservice.service.ShiftTemplateService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-templates")
@RequiredArgsConstructor
public class ShiftTemplateController {

    private final ShiftTemplateService shiftTemplateService;

    @PostMapping
    public ResponseEntity<ShiftTemplateResponse> create(
            @Valid @RequestBody ShiftTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftTemplateService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ShiftTemplateResponse>> getAll(
            @ParameterObject @ModelAttribute ShiftTemplateFilter filter) {
        return ResponseEntity.ok(shiftTemplateService.getAll(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftTemplateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shiftTemplateService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftTemplateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ShiftTemplateRequest request) {
        return ResponseEntity.ok(shiftTemplateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shiftTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
