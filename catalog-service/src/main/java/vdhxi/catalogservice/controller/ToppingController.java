package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.request.ToppingRequest;
import vdhxi.catalogservice.dto.response.ToppingResponse;
import vdhxi.catalogservice.service.ToppingService;

import java.util.List;

@RestController
@RequestMapping("/api/toppings")
@RequiredArgsConstructor
public class ToppingController {
    private final ToppingService service;

    @PostMapping
    public ApiResponse<ToppingResponse> create(@RequestBody ToppingRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<ToppingResponse> update(@PathVariable Long id, @RequestBody ToppingRequest request) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.update(id, request), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ToppingResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getById(id), null);
    }

    @GetMapping
    public ApiResponse<List<ToppingResponse>> getAll() {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getAll(), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}
