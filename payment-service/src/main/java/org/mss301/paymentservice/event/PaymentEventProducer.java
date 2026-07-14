package org.mss301.paymentservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.PaymentStatusEvent;
import org.mss301.commonservice.dto.event.PaymentUrlEvent;
import org.mss301.commonservice.dto.event.enumeration.OrderStatus;
import org.mss301.commonservice.dto.event.enumeration.PaymentGateway;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka Producer publish kết quả thanh toán về order-service.
 *
 * Topic: "payment.status" — kết quả thanh toán (SUCCESS/CANCELLED)
 * Topic: "payment.url"    — payUrl sau khi tạo link thanh toán
 * Consumer: SagaEventConsumer / PaymentUrlReplyService trong order-service
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    public static final String TOPIC_PAYMENT_STATUS = "payment.status";
    public static final String TOPIC_PAYMENT_URL = "payment.url";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentStatus(
            Long orderId, Long transactionId, Long shopId, OrderStatus status, String message
    ) {
        PaymentStatusEvent event = PaymentStatusEvent.builder()
                .orderId(orderId)
                .transactionId(transactionId)
                .shopId(shopId)
                .status(status)
                .message(message)
                .build();

        // Key = orderId để đảm bảo event của cùng 1 order vào cùng 1 partition → ordered
        kafkaTemplate.send(TOPIC_PAYMENT_STATUS, String.valueOf(orderId), event);
        log.info("[Kafka] Published '{}': orderId={}, status={}", TOPIC_PAYMENT_STATUS, orderId, status);
    }

    public void publishPaymentUrl(Long orderId, String payUrl, PaymentGateway gateway) {
        PaymentUrlEvent event = PaymentUrlEvent.builder()
                .orderId(orderId)
                .payUrl(payUrl)
                .gateway(gateway)
                .build();

        kafkaTemplate.send(TOPIC_PAYMENT_URL, String.valueOf(orderId), event);
        log.info("[Kafka] Published '{}': orderId={}, gateway={}", TOPIC_PAYMENT_URL, orderId, gateway);
    }
}

