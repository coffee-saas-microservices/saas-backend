package org.mss301.subscriptionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.subscriptionservice.dto.request.SubscribeRequest;
import org.mss301.subscriptionservice.dto.response.SubscribeResponse;
import org.mss301.subscriptionservice.service.SubscriptionPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionPurchaseService subscriptionPurchaseService;

    /** Shop chọn gói và bấm mua → trả về link thanh toán (mock). */
    @PostMapping("/checkout")
    public ResponseEntity<SubscribeResponse> checkout(@Valid @RequestBody SubscribeRequest request) {
        return ResponseEntity.ok(subscriptionPurchaseService.checkout(request));
    }
}
