package org.mss301.commonservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.commonservice.dto.event.enumeration.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatusEvent {
    private Long orderId;
    private Long shopId;
    private OrderStatus status;
    private String message;
}
