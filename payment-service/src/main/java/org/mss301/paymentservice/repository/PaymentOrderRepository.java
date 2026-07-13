package org.mss301.paymentservice.repository;

import org.mss301.paymentservice.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByOrderCode(String orderCode);

    // Kiểm tra idempotency theo orderCode MoM (String)
    Boolean existsByReferenceId(String referenceId);

    Optional<PaymentOrder> findByOrderId(Long orderId);

    Boolean existsByOrderId(Long orderId);
}

