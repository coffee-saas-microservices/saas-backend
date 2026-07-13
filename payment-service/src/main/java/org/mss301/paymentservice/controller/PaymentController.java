package org.mss301.paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.paymentservice.dto.request.CreatePaymentRequest;
import org.mss301.paymentservice.dto.request.CreateOrderPaymentRequest;
import org.mss301.paymentservice.dto.response.PaymentResponse;
import org.mss301.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @PostMapping("/order")
    public ResponseEntity<PaymentResponse> createPaymentForOrder(@Valid @RequestBody CreateOrderPaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPaymentForOrder(request));
    }

    @PostMapping("/{orderCode}/confirm")
    public ResponseEntity<PaymentResponse> confirm(@PathVariable String orderCode) {
        return ResponseEntity.ok(paymentService.confirmPayment(orderCode));
    }

    @GetMapping("/{orderCode}/confirm")
    public ResponseEntity<PaymentResponse> confirmViaGet(@PathVariable String orderCode) {
        return ResponseEntity.ok(paymentService.confirmPayment(orderCode));
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<PaymentResponse> get(@PathVariable String orderCode) {
        return ResponseEntity.ok(paymentService.getByOrderCode(orderCode));
    }
}
