package org.mss301.inventoryservice.service.inter;

import org.mss301.commonservice.dto.event.OrderCreatedEvent;

public interface StockDeductionService {
    void deductStockForOrder(OrderCreatedEvent event, Long orderId, Long shopId);
}
