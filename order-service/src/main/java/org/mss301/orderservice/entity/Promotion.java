package org.mss301.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.DiscountType;
import org.mss301.orderservice.entity.enumeration.PromotionStatus;
import org.mss301.orderservice.entity.enumeration.PromotionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long promotionId;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "promotion_code", unique = true, nullable = false, length = 50)
    String promotionCode;

    @Column(name = "promotion_name", length = 50)
    String promotionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type")
    PromotionType promotionType;

    @Column(name = "minimum_spent")
    Integer minimumSpent;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    DiscountType discountType;

    @Column(name = "discount_value")
    Long discountValue;

    @Column(name = "max_discount_amount")
    Long maxDiscountAmount;

    @Column(name = "usage_limit_per_user")
    Integer usageLimitPerUser;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Column(name = "quantities")
    Integer quantity;

    @Enumerated(EnumType.STRING)
    PromotionStatus status;
    
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
