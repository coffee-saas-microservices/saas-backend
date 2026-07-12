package org.mss301.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.commonservice.dto.response.PageMeta;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.orderservice.dto.request.OrderRequest;
import org.mss301.orderservice.dto.response.OrderResponse;
import org.mss301.orderservice.entity.enumeration.OrderStatus;
import org.mss301.orderservice.service.inter.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ApiResponse.success(
                HttpStatus.CREATED,
                "Tạo đơn hàng thành công",
                response,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(@ModelAttribute BaseFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        Page<OrderResponse> page = orderService.getAllOrders(filter, shopId);

        PageMeta meta = PageMeta.builder()
                .currentPage(page.getNumber() + 1)
                .size(page.getSize())
                .lastPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();

        return ApiResponse.success(
                HttpStatus.OK,
                "Lấy danh sách đơn hàng thành công",
                page.getContent(),
                meta
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ApiResponse.success(
                HttpStatus.OK,
                "Lấy chi tiết đơn hàng thành công",
                response,
                null
        );
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ApiResponse.success(
                HttpStatus.OK,
                "Cập nhật trạng thái đơn hàng thành công",
                response,
                null
        );
    }

    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<OrderResponse>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @ModelAttribute BaseFilter filter) {
        Page<OrderResponse> page = orderService.getOrdersByCustomer(customerId, filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(page.getNumber() + 1)
                .size(page.getSize())
                .lastPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();

        return ApiResponse.success(
                HttpStatus.OK,
                "Lấy danh sách đơn hàng của khách hàng thành công",
                page.getContent(),
                meta
        );
    }
}
