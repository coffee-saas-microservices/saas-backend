package org.mss301.shopservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.shopservice.entity.enumeration.BillingCycle;
import org.mss301.shopservice.entity.enumeration.SubscriptionTransactionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_transaction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long subscriptionTransactionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_subscription_id")
    ShopSubscription shopSubscription;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "amount")
    Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle")
    BillingCycle billingCycle;

    // Mã đơn bên payment-service trả về
    @Column(name = "payment_order_code")
    String paymentOrderCode;

    @Column(name = "payment_gateway")
    String paymentGateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    SubscriptionTransactionStatus status;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = SubscriptionTransactionStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
