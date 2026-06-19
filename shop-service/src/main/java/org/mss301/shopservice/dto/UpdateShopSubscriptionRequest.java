package org.mss301.shopservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payload mà subscription-service gửi sang để cập nhật gói hiện tại của shop
 * sau khi thanh toán thành công ("lên pro").
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShopSubscriptionRequest {
    private Long currentPlanId;
    private String currentPlanName;
    private String subscriptionStatus;   // ví dụ: ACTIVE
    private LocalDateTime subscriptionEndedAt;
}
