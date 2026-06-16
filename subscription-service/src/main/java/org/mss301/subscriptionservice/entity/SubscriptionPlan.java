package org.mss301.subscriptionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.mss301.subscriptionservice.entity.enumeration.SubscriptionPlanStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plan")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long subscriptionPlanId;

    @Column(name = "subscription_plan_name")
    String subscriptionPlanName;

    @Column(name = "subscription_plan_description")
    String subscriptionPlanDescription;

    @Column(name = "price_monthly")
    Long priceMonthly;

    @Column(name = "price_yearly")
    Long priceYearly;

    @Column(name = "config_limit", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    String configLimit;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    SubscriptionPlanStatus subscriptionPlanStatus;

    @PrePersist
    public void onCreate() {
        if (subscriptionPlanStatus == null) {
            subscriptionPlanStatus = SubscriptionPlanStatus.ACTIVE;
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
