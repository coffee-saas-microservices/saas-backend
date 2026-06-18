package org.mss301.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.commonservice.dto.response.PageMeta;
import org.mss301.inventoryservice.dto.filter.InventoryInvoiceFilter;
import org.mss301.inventoryservice.dto.request.InventoryInvoiceRequest;
import org.mss301.inventoryservice.dto.response.InventoryInvoiceResponse;
import org.mss301.inventoryservice.service.inter.InventoryInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/invoices")
@RequiredArgsConstructor
public class InventoryInvoiceController {

    private final InventoryInvoiceService invoiceService;

    @PostMapping
    public ApiResponse<InventoryInvoiceResponse> importStock(
            @RequestBody @Valid InventoryInvoiceRequest request
    ) {
        InventoryInvoiceResponse response = invoiceService.importStock(request);

        return ApiResponse.success(
                HttpStatus.CREATED,
                "Import stock successfully",
                response,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<InventoryInvoiceResponse>> getByFilter(
            @ModelAttribute InventoryInvoiceFilter filter
    ) {
        Page<InventoryInvoiceResponse> responses = invoiceService.getAll(filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(responses.getNumber() + 1)
                .size(responses.getSize())
                .lastPage(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();

        return ApiResponse.success(
                HttpStatus.OK,
                "Get invoices successfully",
                responses.getContent(),
                meta
        );
    }

    @GetMapping("{id}")
    public ApiResponse<InventoryInvoiceResponse> getDetail(
            @PathVariable Long id
    ) {
        InventoryInvoiceResponse response = invoiceService.getDetail(id);

        return ApiResponse.success(
                HttpStatus.OK,
                "Get invoice detail successfully",
                response,
                null
        );
    }
}
