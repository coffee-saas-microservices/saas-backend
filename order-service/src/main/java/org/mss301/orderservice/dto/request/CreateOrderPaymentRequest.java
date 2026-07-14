package org.mss301.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderPaymentRequest {
    private Long orderId;
    private String orderCode;
    private Long amount;
    private String paymentGateway;
}
