package org.mss301.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.commonservice.dto.response.PageMeta;
import org.mss301.inventoryservice.dto.filter.StockCheckSessionFilter;
import org.mss301.inventoryservice.dto.request.StockCheckApproveRequest;
import org.mss301.inventoryservice.dto.request.StockCheckStartRequest;
import org.mss301.inventoryservice.dto.request.StockCheckUpdateRequest;
import org.mss301.inventoryservice.dto.response.StockCheckSessionResponse;
import org.mss301.inventoryservice.service.inter.StockCheckService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/stock-checks")
@RequiredArgsConstructor
public class StockCheckController {

    private final StockCheckService stockCheckService;

    @PostMapping("start")
    public ApiResponse<StockCheckSessionResponse> startSession(
            @RequestBody @Valid StockCheckStartRequest request
    ) {
        StockCheckSessionResponse response = stockCheckService.startSession(request);

        return ApiResponse.success(
                HttpStatus.CREATED,
                "Start stock check session successfully",
                response,
                null
        );
    }

    @PutMapping("update-count")
    public ApiResponse<StockCheckSessionResponse> updateCount(
            @RequestBody @Valid StockCheckUpdateRequest request
    ) {
        StockCheckSessionResponse response = stockCheckService.updateCount(request);

        return ApiResponse.success(
                HttpStatus.OK,
                "Update stock counts successfully",
                response,
                null
        );
    }

    @PostMapping("approve")
    public ApiResponse<StockCheckSessionResponse> approveSession(
            @RequestBody @Valid StockCheckApproveRequest request
    ) {
        StockCheckSessionResponse response = stockCheckService.approveSession(request);

        return ApiResponse.success(
                HttpStatus.OK,
                "Approve stock check session successfully",
                response,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<StockCheckSessionResponse>> getByFilter(
            @ModelAttribute StockCheckSessionFilter filter
    ) {
        Page<StockCheckSessionResponse> responses = stockCheckService.getAll(filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(responses.getNumber() + 1)
                .size(responses.getSize())
                .lastPage(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();

        return ApiResponse.success(
                HttpStatus.OK,
                "Get stock check sessions successfully",
                responses.getContent(),
                meta
        );
    }
}
