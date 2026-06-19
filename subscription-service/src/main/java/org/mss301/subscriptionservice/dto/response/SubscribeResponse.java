package org.mss301.subscriptionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeResponse {
    private Long shopSubscriptionId;
    private Long subscriptionTransactionId;
    private String status;            // PENDING khi vừa tạo
    private Long amount;
    private String paymentOrderCode;
    // Link thanh toán (mock) — mở/POST vào đây để hoàn tất
    private String payUrl;
}
