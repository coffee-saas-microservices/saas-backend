package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.request.RecipeRequest;
import vdhxi.catalogservice.dto.response.RecipeResponse;
import vdhxi.catalogservice.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService service;

    @PostMapping
    public ApiResponse<RecipeResponse> create(@RequestBody RecipeRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @GetMapping("/variant/{variantId}")
    public ApiResponse<List<RecipeResponse>> getByProductVariant(@PathVariable Long variantId) {
        return ApiResponse.success(HttpStatus.OK, "Success", service.getByProductVariant(variantId), null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}
