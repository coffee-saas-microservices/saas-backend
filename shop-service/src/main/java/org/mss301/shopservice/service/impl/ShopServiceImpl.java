package org.mss301.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.shopservice.dto.ShopResponse;
import org.mss301.shopservice.repository.ShopRepository;
import org.mss301.shopservice.service.ShopService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    @Override
    public Optional<ShopResponse> findByDomain(String domain) {
        return shopRepository.findByDomain(domain)
                .map(shop -> ShopResponse.builder()
                        .id(shop.getId())
                        .shopName(shop.getShopName())
                        .domain(shop.getDomain())
                        .status(shop.getShopStatus() != null ? shop.getShopStatus().name() : null)
                        .build());
    }
}
