package org.mss301.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.DiscountType;
import org.mss301.orderservice.entity.enumeration.PromotionType;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionRequest {

    @NotBlank(message = "Mã khuyến mãi không được trống")
    String promotionCode;

    @NotBlank(message = "Tên khuyến mãi không được trống")
    String promotionName;

    @NotNull(message = "Loại khuyến mãi không được trống")
    PromotionType promotionType;

    @NotNull(message = "Loại giảm giá không được trống")
    DiscountType discountType;

    @NotNull(message = "Giá trị giảm không được trống")
    @Min(value = 0, message = "Giá trị giảm phải >= 0")
    Long discountValue;

    Long maxDiscountAmount;

    @Min(value = 0, message = "Mức chi tiêu tối thiểu phải >= 0")
    Integer minimumSpent;

    Integer usageLimitPerUser;

    String imageUrl;

    LocalDateTime startDate;

    LocalDateTime endDate;

    @Min(value = 1, message = "Số lượng mã phải >= 1")
    Integer quantity;
}
