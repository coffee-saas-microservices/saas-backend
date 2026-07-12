package org.mss301.orderservice.repository;

import org.mss301.orderservice.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByPromotionCodeAndShopId(String promotionCode, Long shopId);
    Page<Promotion> findAllByShopId(Long shopId, Pageable pageable);
    boolean existsByPromotionCodeAndShopId(String promotionCode, Long shopId);
}
