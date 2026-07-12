package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.common.dto.response.PageMeta;
import vdhxi.catalogservice.dto.filter.ProductFilter;
import vdhxi.catalogservice.dto.request.ProductRequest;
import vdhxi.catalogservice.dto.response.ProductResponse;
import vdhxi.catalogservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @PostMapping
    public ApiResponse<ProductResponse> create(@RequestBody ProductRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.update(id, request), null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getById(id), null);
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAll(@ModelAttribute ProductFilter filter) {
        Page<ProductResponse> responses = service.getAll(filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(responses.getNumber() + 1)
                .size(responses.getSize())
                .lastPage(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();

        return ApiResponse.success(HttpStatus.OK, "Get products successfully", responses.getContent(), meta);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }

    @PostMapping("/{id}/allow-toppings")
    public ApiResponse<Void> updateAllowToppings(@PathVariable Long id, @RequestBody List<Long> toppingIds) {
        service.updateAllowToppings(id, toppingIds);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}

