package org.mss301.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayUrlResponse {
    private Long orderId;
    private String payUrl;
    private Boolean ready;
}
