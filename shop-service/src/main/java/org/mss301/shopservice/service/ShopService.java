package org.mss301.shopservice.service;

import org.mss301.shopservice.dto.ShopResponse;

import java.util.Optional;

public interface ShopService {
    Optional<ShopResponse> findByDomain(String domain);
}
