package org.mss301.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.shopservice.dto.ShopResponse;
import org.mss301.shopservice.dto.UpdateShopSubscriptionRequest;
import org.mss301.shopservice.entity.Shop;
import org.mss301.shopservice.repository.ShopRepository;
import org.mss301.shopservice.service.ShopService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public ShopResponse updateSubscription(Long shopId, UpdateShopSubscriptionRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new BusinessException("Cửa hàng không tồn tại"));

        shop.setCurrentPlanId(request.getCurrentPlanId());
        shop.setCurrentPlanName(request.getCurrentPlanName());
        shop.setSubscriptionStatus(request.getSubscriptionStatus());
        shop.setSubscriptionEndedAt(request.getSubscriptionEndedAt());
        shop.setUpdatedAt(LocalDateTime.now());

        Shop saved = shopRepository.save(shop);

        return ShopResponse.builder()
                .id(saved.getId())
                .shopName(saved.getShopName())
                .domain(saved.getDomain())
                .status(saved.getShopStatus() != null ? saved.getShopStatus().name() : null)
                .build();
    }
}
