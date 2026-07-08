package org.mss301.subscriptionservice.repository;

import org.mss301.subscriptionservice.entity.SubscriptionPlan;
import org.mss301.subscriptionservice.entity.enumeration.SubscriptionPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findAllBySubscriptionPlanStatus(SubscriptionPlanStatus status);

    // Lấy gói theo id NHƯNG chỉ khi đúng trạng thái (vd ACTIVE) -> bỏ qua gói đã xóa mềm
    Optional<SubscriptionPlan> findBySubscriptionPlanIdAndSubscriptionPlanStatus(
            Long subscriptionPlanId, SubscriptionPlanStatus status);
}
