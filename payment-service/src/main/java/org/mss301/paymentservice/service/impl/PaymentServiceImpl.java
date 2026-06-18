package org.mss301.paymentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.paymentservice.client.SubscriptionCallbackClient;
import org.mss301.paymentservice.dto.request.CreatePaymentRequest;
import org.mss301.paymentservice.dto.request.PaymentResultRequest;
import org.mss301.paymentservice.dto.response.PaymentResponse;
import org.mss301.paymentservice.entity.PaymentOrder;
import org.mss301.paymentservice.entity.enumeration.PaymentStatus;
import org.mss301.paymentservice.repository.PaymentOrderRepository;
import org.mss301.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String REFERENCE_SUBSCRIPTION = "SUBSCRIPTION";

    private final PaymentOrderRepository paymentOrderRepository;
    private final SubscriptionCallbackClient subscriptionCallbackClient;

    @Value("${payment.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BusinessException("Số tiền thanh toán không hợp lệ");
        }

        String orderCode = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PaymentOrder order = PaymentOrder.builder()
                .orderCode(orderCode)
                .amount(request.getAmount())
                .description(request.getDescription())
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .status(PaymentStatus.PENDING)
                .build();

        PaymentOrder saved = paymentOrderRepository.save(order);
        log.info("Tạo payment order {} số tiền {}", orderCode, request.getAmount());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse confirmPayment(String orderCode) {
        PaymentOrder order = paymentOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new BusinessException("Đơn thanh toán không tồn tại"));

        // Idempotent: đã PAID rồi thì trả về luôn, không callback lại
        if (order.getStatus() == PaymentStatus.PAID) {
            return toResponse(order);
        }

        order.setStatus(PaymentStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        PaymentOrder saved = paymentOrderRepository.save(order);
        log.info("Đã xác nhận thanh toán cho order {}", orderCode);

        // Callback về service nguồn (chỉ subscription hiện tại)
        if (REFERENCE_SUBSCRIPTION.equalsIgnoreCase(saved.getReferenceType())) {
            try {
                subscriptionCallbackClient.notifyPaymentResult(PaymentResultRequest.builder()
                        .referenceId(saved.getReferenceId())
                        .orderCode(saved.getOrderCode())
                        .success(true)
                        .build());
            } catch (Exception e) {
                log.error("Callback sang subscription-service thất bại cho order {}", orderCode, e);
            }
        }

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByOrderCode(String orderCode) {
        PaymentOrder order = paymentOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new BusinessException("Đơn thanh toán không tồn tại"));
        return toResponse(order);
    }

    private PaymentResponse toResponse(PaymentOrder order) {
        return PaymentResponse.builder()
                .paymentOrderId(order.getPaymentOrderId())
                .orderCode(order.getOrderCode())
                .amount(order.getAmount())
                .description(order.getDescription())
                .status(order.getStatus().name())
                .referenceType(order.getReferenceType())
                .referenceId(order.getReferenceId())
                .payUrl(baseUrl + "/api/payments/" + order.getOrderCode() + "/confirm")
                .build();
    }
}
