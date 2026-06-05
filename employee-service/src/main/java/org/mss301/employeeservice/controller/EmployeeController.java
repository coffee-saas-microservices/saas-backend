package org.mss301.employeeservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.employeeservice.dto.request.EmployeeFilter;
import org.mss301.employeeservice.dto.request.EmployeeRequest;
import org.mss301.employeeservice.dto.request.UpdateEmployeeRequest;
import org.mss301.employeeservice.dto.response.EmployeeResponse;
import org.mss301.employeeservice.service.EmployeeService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> getAllEmployees(
            @ParameterObject @ModelAttribute EmployeeFilter filter) {
        return ResponseEntity.ok(employeeService.getAllEmployees(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Xóa nhân viên thành công");
    }
}
