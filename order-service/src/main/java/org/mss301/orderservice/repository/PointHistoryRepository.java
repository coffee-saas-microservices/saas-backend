package org.mss301.orderservice.repository;

import org.mss301.orderservice.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByCustomerIdAndShopId(Long customerId, Long shopId);
}
