package org.mss301.inventoryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "shop-service")
public interface ShopServiceClient {

    @GetMapping("/api/internal/shops/by-domain")
    ResponseEntity<Map<String, Object>> getShopByDomain(@RequestParam("domain") String domain);
}
