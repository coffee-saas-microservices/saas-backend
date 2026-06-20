package org.mss301.shopservice.client;

import org.mss301.shopservice.client.dto.CreatePaymentRequest;
import org.mss301.shopservice.client.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payments")
    PaymentResponse createPayment(@RequestBody CreatePaymentRequest request);
}
