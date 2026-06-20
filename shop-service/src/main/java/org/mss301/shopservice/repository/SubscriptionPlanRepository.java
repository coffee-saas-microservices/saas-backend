package org.mss301.shopservice.repository;

import org.mss301.shopservice.entity.SubscriptionPlan;
import org.mss301.shopservice.entity.enumeration.SubscriptionPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findAllBySubscriptionPlanStatus(SubscriptionPlanStatus status);
}
