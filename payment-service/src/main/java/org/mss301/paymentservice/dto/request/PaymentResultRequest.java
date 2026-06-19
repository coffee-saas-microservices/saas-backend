package org.mss301.paymentservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload payment-service gửi (callback) về service nguồn khi thanh toán xong.
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
