package org.mss301.inventoryservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.InventoryStatusEvent;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


/**
 * Kafka Producer publish kết quả trừ kho về order-service.
 *
 * Topic: "inventory.status"
 * Consumer: SagaEventConsumer trong order-service
 *
 * Key = orderId → đảm bảo event của cùng 1 order vào cùng 1 partition → ordered delivery.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private static final String TOPIC_INVENTORY_STATUS = "inventory.status";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryStatus(Long orderId, Long shopId, OrderStepStatus status, String message) {
        InventoryStatusEvent event = InventoryStatusEvent.builder()
                .orderId(orderId)
                .shopId(shopId)
                .status(status)
                .message(message)
                .build();
        // Dùng orderId làm partition key → cùng order → cùng partition → đảm bảo thứ tự
        kafkaTemplate.send(TOPIC_INVENTORY_STATUS, String.valueOf(orderId), event);
        log.info("[Kafka] Published '{}': orderId={}, status={}",
                TOPIC_INVENTORY_STATUS, orderId, status);
    }
}