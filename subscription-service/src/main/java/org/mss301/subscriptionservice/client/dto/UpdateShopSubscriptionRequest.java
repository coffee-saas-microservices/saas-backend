package org.mss301.subscriptionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Khớp với UpdateShopSubscriptionRequest của shop-service. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShopSubscriptionRequest {
    private Long currentPlanId;
    private String currentPlanName;
    private String subscriptionStatus;
    private LocalDateTime subscriptionEndedAt;
}
