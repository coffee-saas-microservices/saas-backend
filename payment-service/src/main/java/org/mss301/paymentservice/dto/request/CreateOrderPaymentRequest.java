package org.mss301.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderPaymentRequest {
    @NotNull(message = "OrderId không được để trống")
    private Long orderId;

    @NotBlank(message = "OrderCode không được để trống")
    private String orderCode;

    @NotNull(message = "Số tiền không được để trống")
    private Long amount;

    @NotBlank(message = "PaymentGateway không được để trống")
    private String paymentGateway;
}

