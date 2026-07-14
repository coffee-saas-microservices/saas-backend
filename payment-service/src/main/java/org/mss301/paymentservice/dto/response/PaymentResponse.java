package org.mss301.paymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.paymentservice.entity.enumeration.ReferenceType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long paymentOrderId;
    private String orderCode;
    private Long amount;
    private String description;
    private String status;
    private ReferenceType referenceType;
    private String referenceId;
    // Link thanh toán giả lập — mở/POST vào link này để xác nhận đã trả tiền
    private String payUrl;
}
