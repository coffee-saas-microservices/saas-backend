package org.mss301.shopservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.shopservice.entity.enumeration.SubscriptionPlanStatus;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanRequest {

    @NotBlank(message = "Tên gói dịch vụ không được để trống")
    private String subscriptionPlanName;

    private String subscriptionPlanDescription;

    @NotNull(message = "Vui lòng nhập giá tháng")
    private Long priceMonthly;

    @NotNull(message = "Vui lòng nhập giá năm")
    private Long priceYearly;

    private Map<String, Object> configLimit;

    private SubscriptionPlanStatus subscriptionPlanStatus;
}
