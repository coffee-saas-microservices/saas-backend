package org.mss301.subscriptionservice.entity;

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
import org.mss301.subscriptionservice.entity.enumeration.BillingCycle;
import org.mss301.subscriptionservice.entity.enumeration.ShopSubscriptionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop_subscription")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shopSubscriptionId;

    // shopId thuộc shop-service → chỉ lưu ID, không @ManyToOne xuyên service
    @Column(name = "shop_id", nullable = false)
    Long shopId;

    // SubscriptionPlan nằm cùng service/DB nên giữ quan hệ JPA bình thường
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_plan_id")
    SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle")
    BillingCycle billingCycle;

    @Column(name = "price")
    Long price;

    @Column(name = "auto_renewal")
    Boolean autoRenewal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ShopSubscriptionStatus status;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Column(name = "ended_at")
    LocalDateTime endedAt;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = ShopSubscriptionStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
