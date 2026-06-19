package org.mss301.shopservice.service;

import org.mss301.shopservice.dto.ShopResponse;
import org.mss301.shopservice.dto.UpdateShopSubscriptionRequest;

import java.util.Optional;

public interface ShopService {
    Optional<ShopResponse> findByDomain(String domain);

    ShopResponse updateSubscription(Long shopId, UpdateShopSubscriptionRequest request);
}
