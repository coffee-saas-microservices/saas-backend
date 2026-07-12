package org.mss301.orderservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.InventoryStatusEvent;
import org.mss301.commonservice.dto.event.PaymentStatusEvent;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.orderservice.client.IdentityServiceClient;
import org.mss301.orderservice.entity.Order;
import org.mss301.orderservice.entity.PointHistory;
import org.mss301.orderservice.entity.enumeration.OrderItemStatus;
import org.mss301.orderservice.entity.enumeration.OrderStatus;
import org.mss301.orderservice.entity.enumeration.PointHistoryStatus;
import org.mss301.orderservice.repository.OrderRepository;
import org.mss301.orderservice.repository.PointHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final PointHistoryRepository pointHistoryRepository;
    private final IdentityServiceClient identityServiceClient;

    @KafkaListener(
            topics = "payment.status",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentStatus(PaymentStatusEvent event) {
        log.info("[Kafka] Nhận 'payment.status': orderId={}, status={}",
                event.getOrderId(), event.getStatus());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order không tồn tại: {}", event.getOrderId());
                    return new BusinessException("Order không tồn tại: " + event.getOrderId());
                });

        if (event.getStatus() == OrderStepStatus.SUCCESS) {
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
            log.info("Thanh toán OK → orderId={} chuyển sang PROCESSING, đang chờ inventory...", order.getOrderId());
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            order.getItems().forEach(item -> item.setStatus(OrderItemStatus.CANCELLED));
            orderRepository.save(order);
            log.warn("Thanh toán THẤT BẠI → orderId={} CANCELLED", order.getOrderId());
        }
    }

    @KafkaListener(
            topics = "inventory.status",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onInventoryStatus(InventoryStatusEvent event) {
        log.info("[Kafka] Nhận 'inventory.status': orderId={}, status={}",
                event.getOrderId(), event.getStatus());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new BusinessException("Order không tồn tại: " + event.getOrderId()));

        if (event.getStatus() == OrderStepStatus.SUCCESS) {
            order.setStatus(OrderStatus.PAID);
            order.getItems().forEach(item -> item.setStatus(OrderItemStatus.PAID));
            orderRepository.save(order);
            log.info("Order {} HOÀN THÀNH → PAID ✅", order.getOrderId());

            // Tích điểm theo hạng membership của customer
            earnPoints(order);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            order.getItems().forEach(item -> item.setStatus(OrderItemStatus.CANCELLED));
            orderRepository.save(order);
            // Publish event để payment-service hoàn tiền
            orderEventProducer.publishRefundRequested(order);
            log.warn("Hàng KHÔNG ĐỦ → orderId={} CANCELLED. Đã gửi yêu cầu hoàn tiền!", order.getOrderId());
        }
    }


    private void earnPoints(Order order) {
        float pointRate = 1.0f; 
        try {
            var response = identityServiceClient.getUserPointRate(order.getCustomerId());
            if (response != null && response.getBody() != null) {
                pointRate = response.getBody();
            }
        } catch (Exception e) {
            log.warn("Không lấy được pointRate cho customerId={}, dùng default=1.0. Lỗi: {}",
                    order.getCustomerId(), e.getMessage());
        }

        int pointEarned = (int) (order.getPaidPrice() / 1000.0 * pointRate);
        if (pointEarned <= 0) {
            log.info("Đơn hàng {} không đủ giá trị để tích điểm (paidPrice={})",
                    order.getOrderId(), order.getPaidPrice());
            return;
        }

        PointHistory pointHistory = PointHistory.builder()
                .customerId(order.getCustomerId())
                .orderId(order.getOrderId())
                .shopId(order.getShopId())
                .pointChange(pointEarned)
                .status(PointHistoryStatus.EARNED)
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(pointHistory);
        log.info("✅ Tích {} điểm (rate={}) cho customerId={}, orderId={}",
                pointEarned, pointRate, order.getCustomerId(), order.getOrderId());
    }
}
