package org.mss301.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.commonservice.dto.response.PageMeta;
import org.mss301.inventoryservice.dto.filter.RawIngredientFilter;
import org.mss301.inventoryservice.dto.request.RawIngredientRequest;
import org.mss301.inventoryservice.dto.response.RawIngredientResponse;
import org.mss301.inventoryservice.service.inter.RawIngredientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/ingredients")
@RequiredArgsConstructor
public class RawIngredientController {

    private final RawIngredientService rawIngredientService;

    @PostMapping
    public ApiResponse<RawIngredientResponse> create(
            @RequestBody @Valid RawIngredientRequest request
    ) {
        RawIngredientResponse response = rawIngredientService.create(request);

        return ApiResponse.success(
                HttpStatus.CREATED,
                "Create ingredient successfully",
                response,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<RawIngredientResponse>> getByFilter(
            @ModelAttribute RawIngredientFilter filter
    ) {
        Page<RawIngredientResponse> responses = rawIngredientService.getAll(filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(responses.getNumber() + 1)
                .size(responses.getSize())
                .lastPage(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();

        return ApiResponse.success(
                HttpStatus.OK,
                "Get ingredients successfully",
                responses.getContent(),
                meta
        );
    }

    @GetMapping("{id}")
    public ApiResponse<RawIngredientResponse> getDetail(
            @PathVariable Long id
    ) {
        RawIngredientResponse response = rawIngredientService.getDetail(id);

        return ApiResponse.success(
                HttpStatus.OK,
                "Get ingredient detail successfully",
                response,
                null
        );
    }

    @PutMapping("{id}")
    public ApiResponse<RawIngredientResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RawIngredientRequest request
    ) {
        RawIngredientResponse response = rawIngredientService.update(id, request);

        return ApiResponse.success(
                HttpStatus.OK,
                "Update ingredient successfully",
                response,
                null
        );
    }
}
