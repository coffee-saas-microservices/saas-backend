package org.mss301.commonservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundEvent {
    private Long orderId;
    private String orderCode;
    private Long shopId;
    private Long paidPrice;
    private String reason;
}
