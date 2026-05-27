package org.mss301.shopservice.repository;

import org.mss301.shopservice.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByDomain(String domain);
}
