package org.mss301.paymentservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.OrderCreatedEvent;
import org.mss301.commonservice.dto.event.enumeration.OrderStatus;
import org.mss301.commonservice.dto.event.enumeration.PaymentGateway;
import org.mss301.paymentservice.client.OnepayApiClient;
import org.mss301.paymentservice.entity.PaymentOrder;
import org.mss301.paymentservice.entity.enumeration.PaymentStatus;
import org.mss301.paymentservice.entity.enumeration.ReferenceType;
import org.mss301.paymentservice.repository.PaymentOrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer lắng nghe topic "order.created" từ order-service.
 *
 * Khi nhận được event:
 *   1. Lưu PaymentOrder vào DB với status PENDING
 *   2. Nếu paymentGateway = ONEPAY → gọi API tạo payment link
 *   3. Lưu payUrl vào DB
 *   4. Publish PaymentUrlEvent qua topic "payment.url" để order-service nhận payUrl
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final PaymentOrderRepository paymentOrderRepository;
    private final OnepayApiClient onepayApiClient;
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(
            topics = "order.created",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[Kafka] Nhận 'order.created': orderId={}, gateway={}, amount={}",
                event.getOrderId(), event.getPaymentGateway(), event.getPaidPrice());

        // Kiểm tra idempotency: nếu đã có PaymentOrder cho orderId Long này thì bỏ qua
        if (paymentOrderRepository.existsByOrderId(event.getOrderId())) {
            log.warn("[Kafka] Đã tồn tại PaymentOrder cho orderId={}, bỏ qua", event.getOrderId());
            return;
        }

        Long amount = (long) (event.getPaidPrice() != null
                        ? Math.round(event.getPaidPrice())
                        : Math.round(event.getBasePrice()));

        PaymentOrder paymentOrder = PaymentOrder.builder()
                .orderCode(event.getOrderCode())
                .amount(amount)
                .description("Thanh toan don hang " + event.getOrderCode())
                .referenceType(ReferenceType.ORDER)         
                .referenceId(String.valueOf(event.getOrderId()))
                .orderId(event.getOrderId())                  
                .status(PaymentStatus.PENDING)
                .build();

        PaymentOrder saved = paymentOrderRepository.save(paymentOrder);
        log.info("[DB] Tạo PaymentOrder id={} cho orderId={}", saved.getPaymentOrderId(), event.getOrderId());
        String gateway = event.getPaymentGateway();

        if ("ONEPAY".equals(gateway)) {
            handleOnepayPayment(event, saved, amount);
        } else if ("CASH".equals(gateway)) {
            paymentEventProducer.publishPaymentStatus(
                    event.getOrderId(), saved.getPaymentOrderId(), event.getShopId(),
                    OrderStatus.SUCCESS,
                    "Thanh toán tiền mặt"
            );
        }
    }

    private void handleOnepayPayment(OrderCreatedEvent event, PaymentOrder paymentOrder, Long amount) {
        try {
            String payUrl = onepayApiClient.createPaymentLink(
                    event.getOrderId(),
                    paymentOrder.getOrderCode(),
                    amount,
                    paymentOrder.getDescription(),
                    "127.0.0.1"
            );
            paymentOrder.setPayUrl(payUrl);
            paymentOrderRepository.save(paymentOrder);
            log.info("[OnePay] Đã lưu payUrl cho orderId={}", event.getOrderId());
            paymentEventProducer.publishPaymentUrl(event.getOrderId(), payUrl, PaymentGateway.ONEPAY);
        } catch (Exception e) {
            log.error("[OnePay] Tạo link thất bại cho orderId={}: {}", event.getOrderId(), e.getMessage());
            paymentEventProducer.publishPaymentStatus(
                    event.getOrderId(), paymentOrder.getPaymentOrderId(), event.getShopId(),
                    OrderStatus.CANCELLED,
                    "Không thể tạo link thanh toán OnePay: " + e.getMessage()
            );
        }
    }
}
