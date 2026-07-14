package org.mss301.paymentservice.service;

import org.mss301.paymentservice.dto.request.CreatePaymentRequest;
import org.mss301.paymentservice.dto.request.CreateOrderPaymentRequest;
import org.mss301.paymentservice.dto.response.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse confirmPayment(String orderCode);

    PaymentResponse getByOrderCode(String orderCode);

    String processOnepayIpn(Map<String, String> params);

    PaymentResponse createPaymentForOrder(CreateOrderPaymentRequest request);
}

