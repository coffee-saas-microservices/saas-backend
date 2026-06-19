package org.mss301.subscriptionservice.controller;

import lombok.RequiredArgsConstructor;
import org.mss301.subscriptionservice.dto.request.PaymentResultRequest;
import org.mss301.subscriptionservice.service.SubscriptionPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint nội bộ để payment-service gọi callback khi thanh toán xong.
 */
@RestController
@RequestMapping("/api/internal/subscriptions")
@RequiredArgsConstructor
public class InternalSubscriptionController {

    private final SubscriptionPurchaseService subscriptionPurchaseService;

    @PostMapping("/payment-result")
    public ResponseEntity<Void> handlePaymentResult(@RequestBody PaymentResultRequest request) {
        subscriptionPurchaseService.handlePaymentResult(request);
        return ResponseEntity.ok().build();
    }
}
