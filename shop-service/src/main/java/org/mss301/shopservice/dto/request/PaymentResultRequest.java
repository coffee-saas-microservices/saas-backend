package org.mss301.shopservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultRequest {
    private String referenceId;
    private String orderCode;
    private boolean success;
}
