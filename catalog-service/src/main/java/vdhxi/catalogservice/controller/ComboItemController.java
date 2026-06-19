package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.request.ComboItemRequest;
import vdhxi.catalogservice.dto.response.ComboItemResponse;
import vdhxi.catalogservice.service.ComboItemService;

import java.util.List;

@RestController
@RequestMapping("/api/combo-items")
@RequiredArgsConstructor
public class ComboItemController {
    private final ComboItemService service;

    @PostMapping
    public ApiResponse<ComboItemResponse> create(@RequestBody ComboItemRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ComboItemResponse>> getByProductId(@PathVariable Long productId) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getByProductId(productId), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}
