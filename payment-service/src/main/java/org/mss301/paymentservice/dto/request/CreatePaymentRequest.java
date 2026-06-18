package org.mss301.paymentservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Số tiền không được để trống")
    private Long amount;

    private String description;

    // Service nguồn gọi sang (vd "SUBSCRIPTION") + id bản ghi bên đó để callback
    private String referenceType;
    private String referenceId;
}
