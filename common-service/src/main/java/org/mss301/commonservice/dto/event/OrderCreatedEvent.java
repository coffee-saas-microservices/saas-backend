package org.mss301.commonservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private String orderCode;
    private Long shopId;
    private Long customerId;
    private Long basePrice;
    private Long paidPrice;
    private String orderType;
    private String paymentGateway;
    private List<OrderItemEventDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEventDto {
        private Long orderItemId;
        private Long productVariantId;
        private Long shopId;
        private Long unitPrice;
        private Integer quantity;
        private List<ToppingDto> toppings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToppingDto {
        private Long toppingId;
        private Integer quantity;
        private Long unitPrice;
    }
}
