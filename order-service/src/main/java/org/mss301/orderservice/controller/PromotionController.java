package org.mss301.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.commonservice.dto.response.PageMeta;
import org.mss301.orderservice.dto.request.PromotionRequest;
import org.mss301.orderservice.dto.response.PromotionResponse;
import org.mss301.orderservice.entity.enumeration.PromotionStatus;
import org.mss301.orderservice.service.inter.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ApiResponse<PromotionResponse> createPromotion(@RequestBody @Valid PromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        return ApiResponse.success(HttpStatus.CREATED, "Tạo khuyến mãi thành công", response, null);
    }

    @GetMapping
    public ApiResponse<List<PromotionResponse>> getAllPromotions(@ModelAttribute BaseFilter filter) {
        Page<PromotionResponse> page = promotionService.getAllPromotions(filter);
        PageMeta meta = PageMeta.builder()
                .currentPage(page.getNumber() + 1)
                .size(page.getSize())
                .lastPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
        return ApiResponse.success(HttpStatus.OK, "Lấy danh sách khuyến mãi thành công", page.getContent(), meta);
    }

    @GetMapping("/{id}")
    public ApiResponse<PromotionResponse> getPromotionById(@PathVariable Long id) {
        PromotionResponse response = promotionService.getPromotionById(id);
        return ApiResponse.success(HttpStatus.OK, "Lấy chi tiết khuyến mãi thành công", response, null);
    }

    @PutMapping("/{id}")
    public ApiResponse<PromotionResponse> updatePromotion(
            @PathVariable Long id,
            @RequestBody @Valid PromotionRequest request) {
        PromotionResponse response = promotionService.updatePromotion(id, request);
        return ApiResponse.success(HttpStatus.OK, "Cập nhật khuyến mãi thành công", response, null);
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<PromotionResponse> changeStatus(
            @PathVariable Long id,
            @RequestParam PromotionStatus status) {
        PromotionResponse response = promotionService.changeStatus(id, status);
        return ApiResponse.success(HttpStatus.OK, "Cập nhật trạng thái khuyến mãi thành công", response, null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ApiResponse.success(HttpStatus.OK, "Xóa khuyến mãi thành công", null, null);
    }
}
