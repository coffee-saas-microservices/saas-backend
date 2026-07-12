package org.mss301.orderservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.OrderStatus;
import org.mss301.orderservice.entity.enumeration.OrderType;
import org.mss301.commonservice.dto.event.enumeration.PaymentGateway;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    Long orderId;
    Long basePrice;
    Long paidPrice;
    Integer productQuantity;
    OrderType orderType;
    PaymentGateway paymentGateway;
    OrderStatus orderStatus;
    LocalDateTime createdAt;
    List<OrderItemResponse> orderItems;
    String payUrl;
}
