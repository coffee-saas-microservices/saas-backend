package org.mss301.shopservice.repository;

import org.mss301.shopservice.entity.ShopSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopSubscriptionRepository extends JpaRepository<ShopSubscription, Long> {
    List<ShopSubscription> findAllByShopId(Long shopId);
}
