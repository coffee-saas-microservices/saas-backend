package org.mss301.orderservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.OrderType;
import org.mss301.commonservice.dto.event.enumeration.PaymentGateway;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    @NotNull(message = "CustomerId không được trống")
    Long customerId;

    @NotNull(message = "OrderType không được trống")
    OrderType orderType;

    @NotNull(message = "PaymentGateway không được trống")
    PaymentGateway paymentGateway;

    @NotEmpty(message = "Danh sách sản phẩm không được trống")
    @Valid
    List<OrderItemRequest> orderItems;

    String promotionCode;
}
