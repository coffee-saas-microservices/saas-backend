package org.mss301.subscriptionservice.service;

import org.mss301.subscriptionservice.dto.request.SubscriptionPlanRequest;
import org.mss301.subscriptionservice.dto.response.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {
    SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest);

    List<SubscriptionPlanResponse> getAllSubscriptionPlan();

    SubscriptionPlanResponse getSubscriptionPlanById(Long id);

    SubscriptionPlanResponse updateSubscriptionPlan(SubscriptionPlanRequest subscriptionPlanRequest, Long id);

    void deleteSubscriptionPlan(Long id);
}
