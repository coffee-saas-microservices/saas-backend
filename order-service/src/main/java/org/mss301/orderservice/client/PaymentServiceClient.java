package org.mss301.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @GetMapping("/api/payments/{orderCode}")
    Map<String, Object> getPaymentByOrderCode(@PathVariable("orderCode") String orderCode);
}
