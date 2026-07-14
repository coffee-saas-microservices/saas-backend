package org.mss301.commonservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.commonservice.dto.event.enumeration.PaymentGateway;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUrlEvent {
    private Long orderId;
    private String payUrl;
    private PaymentGateway gateway;
}
