package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.request.SizeRequest;
import vdhxi.catalogservice.dto.response.SizeResponse;
import vdhxi.catalogservice.service.SizeService;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {
    private final SizeService service;

    @PostMapping
    public ApiResponse<SizeResponse> create(@RequestBody SizeRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<SizeResponse> update(@PathVariable Long id, @RequestBody SizeRequest request) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.update(id, request), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<SizeResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getById(id), null);
    }

    @GetMapping
    public ApiResponse<List<SizeResponse>> getAll() {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getAll(), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}
