package org.mss301.subscriptionservice.service;

import org.mss301.subscriptionservice.dto.request.PaymentResultRequest;
import org.mss301.subscriptionservice.dto.request.SubscribeRequest;
import org.mss301.subscriptionservice.dto.response.SubscribeResponse;

public interface SubscriptionPurchaseService {

    /** Bước 1: shop chọn gói → tạo subscription PENDING + gọi payment-service tạo link thanh toán. */
    SubscribeResponse checkout(SubscribeRequest request);

    /** Bước 3: payment-service callback khi thanh toán xong → kích hoạt subscription + cập nhật shop. */
    void handlePaymentResult(PaymentResultRequest request);
}
