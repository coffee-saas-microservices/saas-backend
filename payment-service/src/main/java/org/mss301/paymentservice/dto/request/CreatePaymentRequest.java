package org.mss301.paymentservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.paymentservice.entity.enumeration.ReferenceType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Số tiền không được để trống")
    private Long amount;
    private String description;
    private ReferenceType referenceType;
    private String referenceId;
}
