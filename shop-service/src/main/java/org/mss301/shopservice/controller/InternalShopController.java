package org.mss301.shopservice.controller;

import lombok.RequiredArgsConstructor;
import org.mss301.shopservice.dto.ShopResponse;
import org.mss301.shopservice.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
