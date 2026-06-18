package org.mss301.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.inventoryservice.dto.request.UnitConversionRequest;
import org.mss301.inventoryservice.dto.response.UnitConversionResponse;
import org.mss301.inventoryservice.entity.UnitConversion;
import org.mss301.inventoryservice.service.inter.UnitConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/unit-conversions")
@RequiredArgsConstructor
public class UnitConversionController {

    private final UnitConversionService unitConversionService;

    @PostMapping
    public ApiResponse<UnitConversionResponse> create(
            @RequestBody @Valid UnitConversionRequest request) {
        UnitConversionResponse response = unitConversionService.create(request);

        return ApiResponse.success(
                HttpStatus.CREATED,
                "Create unit conversion successfully",
                response,
                null);
    }

    @PutMapping("{id}")
    public ApiResponse<UnitConversion> update(
            @PathVariable Long id,
            @RequestBody @Valid UnitConversionRequest request) {
        UnitConversion response = unitConversionService.update(id, request);

        return ApiResponse.success(
                HttpStatus.OK,
                "Update unit conversion successfully",
                response,
                null);
    }
}
