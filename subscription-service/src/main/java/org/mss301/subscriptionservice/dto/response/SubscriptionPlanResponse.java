package org.mss301.subscriptionservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.subscriptionservice.entity.enumeration.SubscriptionPlanStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanResponse {

    private Long subscriptionPlanId;
    private String subscriptionPlanName;
    private String subscriptionPlanDescription;
    private Long priceMonthly;
    private Long priceYearly;
    private Map<String, Object> configLimit;
    private SubscriptionPlanStatus subscriptionPlanStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
