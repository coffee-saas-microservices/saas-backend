package org.mss301.paymentservice.service;

import org.mss301.paymentservice.dto.request.CreatePaymentRequest;
import org.mss301.paymentservice.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse confirmPayment(String orderCode);

    PaymentResponse getByOrderCode(String orderCode);
}
