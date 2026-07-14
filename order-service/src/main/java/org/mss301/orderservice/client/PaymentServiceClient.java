package org.mss301.orderservice.client;

import org.mss301.orderservice.dto.request.CreateOrderPaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @GetMapping("/api/payments/{orderCode}")
    Map<String, Object> getPaymentByOrderCode(@PathVariable("orderCode") String orderCode);

    @PostMapping("/api/payments/order")
    Map<String, Object> createPaymentForOrder(@RequestBody CreateOrderPaymentRequest request);
}

