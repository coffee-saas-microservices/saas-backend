package org.mss301.orderservice.repository;

import org.mss301.orderservice.entity.PromotionTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionTargetRepository extends JpaRepository<PromotionTarget, Long> {
    List<PromotionTarget> findByPromotion_PromotionId(Long promotionId);
}
