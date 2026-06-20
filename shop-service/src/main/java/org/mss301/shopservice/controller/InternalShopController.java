package org.mss301.shopservice.controller;

import lombok.RequiredArgsConstructor;
import org.mss301.shopservice.dto.ShopResponse;
import org.mss301.shopservice.dto.request.PaymentResultRequest;
import org.mss301.shopservice.service.ShopService;
import org.mss301.shopservice.service.SubscriptionPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/shops")
@RequiredArgsConstructor
public class InternalShopController {

    private final ShopService shopService;
    private final SubscriptionPurchaseService subscriptionPurchaseService;

    @GetMapping("/by-domain")
    public ResponseEntity<ShopResponse> getShopByDomain(@RequestParam("domain") String domain) {
        return shopService.findByDomain(domain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint nội bộ để payment-service gọi callback khi thanh toán xong.
     * (Trước đây nằm ở subscription-service, nay gộp vào đây)
     */
    @PostMapping("/subscriptions/payment-result")
    public ResponseEntity<Void> handlePaymentResult(@RequestBody PaymentResultRequest request) {
        subscriptionPurchaseService.handlePaymentResult(request);
        return ResponseEntity.ok().build();
    }
}
