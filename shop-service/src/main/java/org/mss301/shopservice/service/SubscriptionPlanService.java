package org.mss301.shopservice.service;

import org.mss301.shopservice.dto.request.SubscriptionPlanRequest;
import org.mss301.shopservice.dto.response.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest request);

    List<SubscriptionPlanResponse> getAllSubscriptionPlan();

    SubscriptionPlanResponse getSubscriptionPlanById(Long id);

    SubscriptionPlanResponse updateSubscriptionPlan(SubscriptionPlanRequest request, Long id);

    void deleteSubscriptionPlan(Long id);
}
