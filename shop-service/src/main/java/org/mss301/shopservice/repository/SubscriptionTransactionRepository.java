package org.mss301.shopservice.repository;

import org.mss301.shopservice.entity.SubscriptionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionTransactionRepository extends JpaRepository<SubscriptionTransaction, Long> {
    Optional<SubscriptionTransaction> findByPaymentOrderCode(String paymentOrderCode);
}
