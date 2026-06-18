package org.mss301.subscriptionservice.client;

import org.mss301.subscriptionservice.client.dto.UpdateShopSubscriptionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shop-service")
public interface ShopServiceClient {

    @PutMapping("/api/internal/shops/{id}/subscription")
    void updateShopSubscription(
            @PathVariable("id") Long shopId,
            @RequestBody UpdateShopSubscriptionRequest request);
}
