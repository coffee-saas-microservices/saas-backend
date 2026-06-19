package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.request.ProductVariantRequest;
import vdhxi.catalogservice.dto.response.ProductVariantResponse;
import vdhxi.catalogservice.service.ProductVariantService;

import java.util.List;

@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService service;

    @PostMapping
    public ApiResponse<ProductVariantResponse> create(@RequestBody ProductVariantRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductVariantResponse> update(@PathVariable Long id, @RequestBody ProductVariantRequest request) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.update(id, request), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductVariantResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getById(id), null);
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductVariantResponse>> getAllByProduct(@PathVariable Long productId) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getAllByProduct(productId), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}
