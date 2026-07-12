package org.mss301.orderservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.OrderCreatedEvent;
import org.mss301.commonservice.dto.event.OrderRefundEvent;
import org.mss301.orderservice.entity.Order;
import org.mss301.orderservice.entity.enumeration.ToppingPerOrderItemStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String TOPIC_ORDER_CREATED = "order.created";
    public static final String TOPIC_ORDER_REFUND_REQUESTED = "order.refund.requested";

    public void publishOrderCreated(Order order) {
        //map từ entity sang event dto
        //truyền thằng entity vào Kafka sẽ bị Serialize vì có lazy loaded
        List<OrderCreatedEvent.OrderItemEventDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    List<OrderCreatedEvent.ToppingDto> toppings = item.getToppings().stream()
                            .filter(t -> t.getStatus() == ToppingPerOrderItemStatus.ACTIVE)
                            .map(t -> OrderCreatedEvent.ToppingDto.builder()
                                    .toppingId(t.getToppingId())
                                    .quantity(t.getQuantity())
                                    .unitPrice(t.getUnitPrice())
                                    .build())
                            .toList();

                    return OrderCreatedEvent.OrderItemEventDto.builder()
                            .orderItemId(item.getOrderItemId())
                            .productVariantId(item.getProductVariantId())
                            .unitPrice(item.getUnitPrice())
                            .quantity(item.getQuantity())
                            .toppings(toppings)
                            .build();
                })
                .toList();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getCode())
                .shopId(order.getShopId())
                .customerId(order.getCustomerId())
                .basePrice(order.getBasePrice())
                .paidPrice(order.getPaidPrice())
                .orderType(order.getOrderType().name())
                .paymentGateway(order.getPaymentGateway().name())
                .items(itemDtos)
                .build();
        kafkaTemplate.send(TOPIC_ORDER_CREATED, String.valueOf(event.getOrderId()), event);
        log.info("[Kafka] Published '{}': orderId={}, code={}, gateway={}",
                TOPIC_ORDER_CREATED, order.getOrderId(), order.getCode(), order.getPaymentGateway());
    }

    public void publishRefundRequested(Order order) {
        OrderRefundEvent event = OrderRefundEvent.builder()
                .orderId(order.getOrderId())
                .orderCode(order.getCode())
                .shopId(order.getShopId())
                .paidPrice(order.getPaidPrice())
                .reason("Kho hàng không đủ — tự động hoàn tiền")
                .build();
        kafkaTemplate.send(TOPIC_ORDER_REFUND_REQUESTED, String.valueOf(order.getOrderId()), event);
        log.info("[Kafka] Published '{}': orderId={}, paidPrice={}",
                TOPIC_ORDER_REFUND_REQUESTED, order.getOrderId(), order.getPaidPrice());
    }
}
