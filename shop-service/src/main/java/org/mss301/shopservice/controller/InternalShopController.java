package org.mss301.shopservice.controller;

import lombok.RequiredArgsConstructor;
import org.mss301.shopservice.dto.ShopResponse;
import org.mss301.shopservice.dto.UpdateShopSubscriptionRequest;
import org.mss301.shopservice.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/shops")
@RequiredArgsConstructor
public class InternalShopController {

    private final ShopService shopService;

    @GetMapping("/by-domain")
    public ResponseEntity<ShopResponse> getShopByDomain(@RequestParam("domain") String domain) {
        return shopService.findByDomain(domain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Được subscription-service gọi sau khi thanh toán thành công để "lên pro":
     * cập nhật gói hiện tại + trạng thái subscription của shop.
     */
    @PutMapping("/{id}/subscription")
    public ResponseEntity<ShopResponse> updateSubscription(
            @PathVariable("id") Long id,
            @RequestBody UpdateShopSubscriptionRequest request) {
        return ResponseEntity.ok(shopService.updateSubscription(id, request));
    }
}
