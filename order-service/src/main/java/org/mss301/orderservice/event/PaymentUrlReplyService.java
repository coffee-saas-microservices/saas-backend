package org.mss301.orderservice.event;

import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.PaymentUrlEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Service quản lý cơ chế chờ payUrl reply từ payment-service.
 *
 * Flow:
 *   1. OrderServiceImpl gọi registerWait(orderId) TRƯỚC khi publish order.created
 *   2. Publish order.created lên Kafka
 *   3. Gọi waitForPayUrl(orderId, timeout) để block chờ reply
 *   4. Kafka listener trên topic "payment.url" nhận PaymentUrlEvent từ payment-service
 *   5. Complete CompletableFuture tương ứng → unblock waitForPayUrl
 */
@Component
@Slf4j
public class PaymentUrlReplyService {

    private final ConcurrentHashMap<Long, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Đăng ký chờ payUrl cho orderId. Phải gọi TRƯỚC khi publish event.
     */
    public void registerWait(Long orderId) {
        pendingRequests.put(orderId, new CompletableFuture<>());
        log.debug("[PaymentUrlReply] Đăng ký chờ payUrl cho orderId={}", orderId);
    }

    public String waitForPayUrl(Long orderId, long timeoutSeconds) {
        CompletableFuture<String> future = pendingRequests.get(orderId);
        if (future == null) {
            log.warn("[PaymentUrlReply] Chưa đăng ký wait cho orderId={}, bỏ qua", orderId);
            return null;
        }
        try {
            String payUrl = future.get(timeoutSeconds, TimeUnit.SECONDS);
            log.info("[PaymentUrlReply] Nhận payUrl cho orderId={}", orderId);
            return payUrl;
        } catch (TimeoutException e) {
            log.warn("[PaymentUrlReply] Timeout chờ payUrl cho orderId={} ({}s)", orderId, timeoutSeconds);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[PaymentUrlReply] Bị interrupt khi chờ payUrl cho orderId={}", orderId);
            return null;
        } catch (ExecutionException e) {
            log.error("[PaymentUrlReply] Lỗi khi chờ payUrl cho orderId={}: {}", orderId, e.getMessage());
            return null;
        } finally {
            pendingRequests.remove(orderId);
        }
    }

    /**
     * Kafka listener nhận PaymentUrlEvent từ payment-service.
     * Complete CompletableFuture tương ứng nếu có request đang chờ.
     */
    @KafkaListener(
            topics = "payment.url",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentUrl(PaymentUrlEvent event) {
        log.info("[Kafka] Nhận 'payment.url': orderId={}, gateway={}", event.getOrderId(), event.getGateway());
        CompletableFuture<String> future = pendingRequests.get(event.getOrderId());
        if (future != null) {
            future.complete(event.getPayUrl());
        } else {
            log.debug("[PaymentUrlReply] Không có request nào đang chờ cho orderId={}", event.getOrderId());
        }
    }
}
