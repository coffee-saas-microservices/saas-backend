package org.mss301.inventoryservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.OrderCreatedEvent;
import org.mss301.commonservice.dto.event.PaymentStatusEvent;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.inventoryservice.service.inter.StockDeductionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka Consumer lắng nghe "payment.status" từ payment-service.
 *
 * ===== VẤN ĐỀ =====
 * Topic "payment.status" chỉ chứa orderId + status, không có danh sách items.
 * Nhưng để trừ kho, ta cần biết sản phẩm nào trong order.
 *
 * ===== GIẢI PHÁP =====
 * inventory-service cũng subscribe "order.created" để lưu cache OrderCreatedEvent.
 * Khi nhận "payment.status" SUCCESS → lấy từ cache → thực hiện trừ kho.
 *
 * ⚠️ WARNING: Cache in-memory chỉ hoạt động với 1 instance.
 * Production: dùng Redis hoặc lưu vào DB (bảng pending_order_deductions).
 * TODO: Thay ConcurrentHashMap bằng Redis cache (@Cacheable) hoặc JPA entity.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusConsumer {

    private final StockDeductionService stockDeductionService;
    private final InventoryEventProducer inventoryEventProducer;
    private ConcurrentHashMap<Long, OrderCreatedEvent> orderCache = new ConcurrentHashMap<>();

    //cache tạm thời orderId -> OrderCreatedEvent
    //đc populateởi onOrderCreated(), consumed bởi onPaymentStatus()

    //B1: listen order.created để cache thông tin order
    //cùng group inventory-service-group với payment.status listener
    //chạy trước payment.status (do order.created được publish trước)
    @KafkaListener(
            topics = "order.created",
            groupId = "inventory-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[Kafka] Nhận 'order.created' → cache: orderId={}, {} items",
                event.getOrderId(), event.getItems() != null ? event.getItems().size() : 0);
        // Lưu vào cache để dùng khi payment.status đến
        orderCache.put(event.getOrderId(), event);
    }

    //B2: listen payment.status từ payment-service
    //SUCCESS -> lấy cached OrderCreatedEvent -> trừ kho -> publish inventory.status SUCCESS
    //CANCCLED -> bỏ qua (order đã bị cancel, ko cần trừ kho)
    @KafkaListener(
            topics = "payment.status",
            groupId = "inventory-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentStatus(PaymentStatusEvent event) {
        log.info("[Kafka] Nhận 'payment.status': orderId={}, status={}",
                event.getOrderId(), event.getStatus());

        if (event.getStatus() != OrderStepStatus.SUCCESS) {
            log.info("[Kafka] Thanh toán FAILED/CANCELLED → không trừ kho cho orderId={}",
                    event.getOrderId());
            orderCache.remove(event.getOrderId()); // Cleanup cache
            return;
        }

        OrderCreatedEvent orderEvent = orderCache.get(event.getOrderId());
        if (orderEvent == null) {
            log.error("[Kafka] ❌ Cache miss cho orderId={}. " +
                    "Service có thể đã restart. Cần implement persistent storage!", event.getOrderId());
            inventoryEventProducer.publishInventoryStatus(
                    event.getOrderId(),
                    event.getShopId(),
                    OrderStepStatus.CANCELLED,
                    "Cache miss: inventory-service không tìm thấy thông tin order. " +
                            "Cần xem xét implement Redis cache."
            );
            return;
        }

        //thực hiện trừ kho
        try {
            Long shopId = event.getShopId() != null
                    ? event.getShopId()
                    : orderEvent.getShopId();
            stockDeductionService.deductStockForOrder(orderEvent, event.getOrderId(), shopId);

            inventoryEventProducer.publishInventoryStatus(
                    event.getOrderId(),
                    orderEvent.getShopId(),
                    OrderStepStatus.SUCCESS,
                    "Trừ kho thành công"
            );
        } catch (BusinessException e) {
            log.error("[Kafka] ❌ Trừ kho THẤT BẠI orderId={}: {}", event.getOrderId(), e.getMessage());
            inventoryEventProducer.publishInventoryStatus(
                    event.getOrderId(),
                    orderEvent.getShopId(),
                    OrderStepStatus.CANCELLED,
                    "Không đủ nguyên liệu" + e.getMessage()
            );
        } finally {
            orderCache.remove(event.getOrderId());
        }
    }
}
