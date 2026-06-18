package org.mss301.paymentservice.client;

import org.mss301.paymentservice.dto.request.PaymentResultRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Gọi callback sang subscription-service khi thanh toán (referenceType = SUBSCRIPTION) thành công.
 * Tìm service qua Eureka theo tên "subscription-service".
 */
@FeignClient(name = "subscription-service")
public interface SubscriptionCallbackClient {

    @PostMapping("/api/internal/subscriptions/payment-result")
    ResponseEntity<Void> notifyPaymentResult(@RequestBody PaymentResultRequest request);
}
