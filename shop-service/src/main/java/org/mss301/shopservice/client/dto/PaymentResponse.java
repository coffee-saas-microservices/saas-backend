package org.mss301.shopservice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Khớp với PaymentResponse của payment-service (chỉ lấy field cần). */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    private String orderCode;
    private Long amount;
    private String status;
    private String payUrl;
}
