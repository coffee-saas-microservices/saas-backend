package org.mss301.orderservice.repository;

import org.mss301.orderservice.entity.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {
    long countByPromotionIdAndCustomerId(Long promotionId, Long customerId);
}
