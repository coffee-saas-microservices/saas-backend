package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.common.dto.response.PageMeta;
import vdhxi.catalogservice.dto.filter.RecipeFilter;
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

    @GetMapping
    public ApiResponse<List<RecipeResponse>> getByProductVariant(@ModelAttribute RecipeFilter filter) {
        Page<RecipeResponse> responses = service.getByProductVariant(filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(responses.getNumber() + 1)
                .size(responses.getSize())
                .lastPage(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();

        return ApiResponse.success(HttpStatus.OK, "Get recipes successfully", responses.getContent(), meta);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}

