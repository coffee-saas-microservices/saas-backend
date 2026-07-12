package org.mss301.orderservice.repository;

import org.mss301.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByShopId(Long shopId, Pageable pageable);
    Page<Order> findAllByShopIdAndCustomerId(Long shopId, Long customerId, Pageable pageable);
}
