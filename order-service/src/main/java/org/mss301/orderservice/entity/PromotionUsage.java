package org.mss301.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.PromotionUsageStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_usage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long promotionUsageId;

    @Column(name = "promotion_id", nullable = false)
    Long promotionId;

    @Column(name = "order_id", nullable = false)
    Long orderId;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "discount_amount")
    Long discountAmount;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    PromotionUsageStatus status;
}
