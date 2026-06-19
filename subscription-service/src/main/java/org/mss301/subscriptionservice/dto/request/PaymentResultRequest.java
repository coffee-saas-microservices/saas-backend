package org.mss301.subscriptionservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload payment-service callback về khi thanh toán xong.
 * referenceId = id của SubscriptionTransaction.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultRequest {
    private String referenceId;
    private String orderCode;
    private boolean success;
}
