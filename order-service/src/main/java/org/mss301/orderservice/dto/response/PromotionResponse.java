package org.mss301.orderservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.DiscountType;
import org.mss301.orderservice.entity.enumeration.PromotionStatus;
import org.mss301.orderservice.entity.enumeration.PromotionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionResponse {

    Long promotionId;
    Long shopId;
    String promotionCode;
    String promotionName;
    PromotionType promotionType;
    DiscountType discountType;
    Long discountValue;
    Long maxDiscountAmount;
    Integer minimumSpent;
    Integer usageLimitPerUser;
    String imageUrl;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer quantity;
    PromotionStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
