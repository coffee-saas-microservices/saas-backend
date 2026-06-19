package org.mss301.subscriptionservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.subscriptionservice.entity.enumeration.BillingCycle;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequest {

    // Ghi chú: trong hệ thống thật shopId lấy từ tenant context;
    // ở đây nhận trực tiếp để test nhanh trên Swagger.
    @NotNull(message = "Vui lòng cung cấp shopId")
    private Long shopId;

    @NotNull(message = "Vui lòng chọn gói dịch vụ")
    private Long subscriptionPlanId;

    @NotNull(message = "Vui lòng chọn chu kỳ thanh toán")
    private BillingCycle billingCycle;

    private Boolean autoRenewal;
}
