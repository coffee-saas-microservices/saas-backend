package org.mss301.commonservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusEvent {
    private Long orderId;
    private Long transactionId;
    private Long shopId;
    private OrderStepStatus status;
    private String message;
}
