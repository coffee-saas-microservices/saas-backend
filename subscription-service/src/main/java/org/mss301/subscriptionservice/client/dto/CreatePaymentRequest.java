package org.mss301.subscriptionservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Khớp với CreatePaymentRequest của payment-service. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    private Long amount;
    private String description;
    private String referenceType;
    private String referenceId;
}
