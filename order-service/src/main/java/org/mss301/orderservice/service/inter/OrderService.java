package org.mss301.orderservice.service.inter;

import org.mss301.commonservice.dto.event.enumeration.OrderStatus;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.orderservice.dto.request.OrderRequest;
import org.mss301.orderservice.dto.response.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    Page<OrderResponse> getAllOrders(BaseFilter filter, Long shopId);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    Page<OrderResponse> getOrdersByCustomer(Long customerId, BaseFilter filter);
}

