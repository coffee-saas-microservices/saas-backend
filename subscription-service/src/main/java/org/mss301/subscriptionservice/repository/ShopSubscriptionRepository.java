package org.mss301.subscriptionservice.repository;

import org.mss301.subscriptionservice.entity.ShopSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopSubscriptionRepository extends JpaRepository<ShopSubscription, Long> {
    List<ShopSubscription> findAllByShopId(Long shopId);
}
