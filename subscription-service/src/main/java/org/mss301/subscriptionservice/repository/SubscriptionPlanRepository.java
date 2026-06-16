package org.mss301.subscriptionservice.repository;

import org.mss301.subscriptionservice.entity.SubscriptionPlan;
import org.mss301.subscriptionservice.entity.enumeration.SubscriptionPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findAllBySubscriptionPlanStatus(SubscriptionPlanStatus status);
}
