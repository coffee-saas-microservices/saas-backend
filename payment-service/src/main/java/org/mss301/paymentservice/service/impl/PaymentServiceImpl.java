package org.mss301.paymentservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.mss301.paymentservice.client.SubscriptionCallbackClient;
import org.mss301.paymentservice.config.OnepayProperties;
import org.mss301.paymentservice.client.OnepayApiClient;
import org.mss301.paymentservice.dto.request.CreatePaymentRequest;
import org.mss301.paymentservice.dto.request.CreateOrderPaymentRequest;
import org.mss301.paymentservice.dto.request.PaymentResultRequest;
import org.mss301.paymentservice.dto.response.PaymentResponse;
import org.mss301.paymentservice.entity.PaymentOrder;
import org.mss301.paymentservice.entity.enumeration.PaymentStatus;
import org.mss301.paymentservice.entity.enumeration.ReferenceType;
import org.mss301.paymentservice.event.PaymentEventProducer;
import org.mss301.paymentservice.repository.PaymentOrderRepository;
import org.mss301.paymentservice.service.PaymentService;
import org.mss301.paymentservice.utils.OnepaySignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    private final PaymentOrderRepository paymentOrderRepository;
    private final SubscriptionCallbackClient subscriptionCallbackClient;
    private final OnepayProperties onepayProperties;
    private final PaymentEventProducer paymentEventProducer;
    private final OnepayApiClient onepayApiClient;

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

        // Callback về service nguồn nếu là SUBSCRIPTION (dùng enum thay vì so sánh String)
        if (ReferenceType.SUBSCRIPTION == saved.getReferenceType()) {
            try {
                subscriptionCallbackClient.notifyPaymentResult(PaymentResultRequest.builder()
                        .referenceId(saved.getReferenceId())
                        .orderCode(saved.getOrderCode())
                        .success(true)
                        .build());
            } catch (Exception e) {
                log.error("Callback sang subscription-service thất bại cho order {}", orderCode, e);
            }
        } else if (ReferenceType.ORDER == saved.getReferenceType()) {
            paymentEventProducer.publishPaymentStatus(
                    saved.getOrderId(),
                    saved.getPaymentOrderId(),
                    null,
                    OrderStepStatus.SUCCESS,
                    "Xác nhận thanh toán thủ công"
            );
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
                .payUrl(order.getPayUrl() != null ? order.getPayUrl() : baseUrl + "/api/payments/" + order.getOrderCode() + "/confirm")
                .build();
    }

    @Override
    @Transactional
    public String processOnepayIpn(Map<String, String> params) {
        log.info("[OnePay IPN] Nhận callback...");
        String vpcSecureHash = params.get("vpc_SecureHash");

        Map<String, String> fields = new HashMap<>(params);
        fields.remove("vpc_SecureHash");

        // Filter and sort parameters starting with vpc_ or user_
        List<String> fieldNames = new ArrayList<>();
        for (String key : fields.keySet()) {
            if (key.startsWith("vpc_") || key.startsWith("user_")) {
                fieldNames.add(key);
            }
        }
        Collections.sort(fieldNames);

        List<String> hashParts = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashParts.add(fieldName + "=" + fieldValue);
            }
        }
        String hashData = String.join("&", hashParts);
        log.info("[OnePay IPN] hashData: {}", hashData);

        String calculatedHash = OnepaySignatureUtil.hmacSHA256(onepayProperties.getHashSecret(), hashData);
        log.info("[OnePay IPN] calculatedHash: {}", calculatedHash);
        log.info("[OnePay IPN] vpcSecureHash from request: {}", vpcSecureHash);

        boolean isValidSignature = calculatedHash.equalsIgnoreCase(vpcSecureHash);

        if (!isValidSignature) {
            log.error("[OnePay IPN] ⚠️ Chữ ký không hợp lệ! Computed hash != {}", vpcSecureHash);
            return "responsecode=0&desc=confirm-fail";
        }

        String orderCode = fields.get("vpc_MerchTxnRef");
        PaymentOrder paymentOrder = paymentOrderRepository.findByOrderCode(orderCode).orElse(null);

        if (paymentOrder == null) {
            log.error("[OnePay IPN] Không tìm thấy PaymentOrder với code={}", orderCode);
            return "responsecode=0&desc=confirm-fail";
        }

        long onepayAmount = Long.parseLong(fields.get("vpc_Amount")) / 100;
        if (paymentOrder.getAmount() != onepayAmount) {
            log.error("[OnePay IPN] Số tiền không khớp! DB={}, OnePay={}", paymentOrder.getAmount(), onepayAmount);
            return "responsecode=0&desc=confirm-fail";
        }

        if (paymentOrder.getStatus() != PaymentStatus.PENDING) {
            // Already processed: return confirm-success so OnePay doesn't retry
            return "responsecode=1&desc=confirm-success";
        }

        String responseCode = fields.get("vpc_TxnResponseCode");
        Long orderId = paymentOrder.getOrderId();

        if ("0".equals(responseCode)) {
            paymentOrder.setStatus(PaymentStatus.PAID);
            paymentOrder.setPaidAt(LocalDateTime.now());
            paymentOrderRepository.save(paymentOrder);
            log.info("[OnePay IPN] ✅ Thanh toán thành công cho orderId={}", orderId);

            paymentEventProducer.publishPaymentStatus(
                    orderId,
                    paymentOrder.getPaymentOrderId(),
                    null,
                    OrderStepStatus.SUCCESS,
                    "Thanh toán OnePay thành công. Mã giao dịch: " + fields.get("vpc_TransactionNo")
            );
        } else {
            paymentOrder.setStatus(PaymentStatus.FAILED);
            paymentOrderRepository.save(paymentOrder);
            log.warn("[OnePay IPN] ❌ Thanh toán thất bại cho orderId={}, ResponseCode={}", orderId, responseCode);

            paymentEventProducer.publishPaymentStatus(
                    orderId,
                    paymentOrder.getPaymentOrderId(),
                    null,
                    OrderStepStatus.CANCELLED,
                    "Thanh toán OnePay thất bại. ResponseCode: " + responseCode
            );
        }
        return "responsecode=1&desc=confirm-success";
    }

    @Override
    @Transactional
    public PaymentResponse createPaymentForOrder(CreateOrderPaymentRequest request) {
        log.info("[REST] Tạo payment cho orderId={}, gateway={}, amount={}",
                request.getOrderId(), request.getPaymentGateway(), request.getAmount());

        // Kiểm tra idempotency: nếu đã có PaymentOrder cho orderCode này thì trả về
        var existingOpt = paymentOrderRepository.findByOrderCode(request.getOrderCode());
        if (existingOpt.isPresent()) {
            log.warn("[REST] Đã tồn tại PaymentOrder cho orderCode={}, trả về luôn", request.getOrderCode());
            return toResponse(existingOpt.get());
        }

        PaymentOrder paymentOrder = PaymentOrder.builder()
                .orderCode(request.getOrderCode())
                .amount(request.getAmount())
                .description("Thanh toan don hang " + request.getOrderCode())
                .referenceType(ReferenceType.ORDER)
                .referenceId(String.valueOf(request.getOrderId()))
                .orderId(request.getOrderId())
                .status(PaymentStatus.PENDING)
                .build();

        PaymentOrder saved = paymentOrderRepository.save(paymentOrder);
        log.info("[DB] Tạo PaymentOrder id={} cho orderId={}", saved.getPaymentOrderId(), request.getOrderId());
        String gateway = request.getPaymentGateway();

        if ("ONEPAY".equals(gateway)) {
            try {
                String payUrl = onepayApiClient.createPaymentLink(
                        request.getOrderId(),
                        saved.getOrderCode(),
                        request.getAmount(),
                        saved.getDescription(),
                        "127.0.0.1"
                );
                saved.setPayUrl(payUrl);
                saved = paymentOrderRepository.save(saved);
                log.info("[OnePay] Đã lưu payUrl cho orderId={}", request.getOrderId());
            } catch (Exception e) {
                log.error("[OnePay] Tạo link thất bại cho orderId={}: {}", request.getOrderId(), e.getMessage());
                throw new BusinessException("Không thể tạo link thanh toán OnePay: " + e.getMessage());
            }
        } else if ("CASH".equals(gateway)) {
            paymentEventProducer.publishPaymentStatus(
                    request.getOrderId(), saved.getPaymentOrderId(), null,
                    OrderStepStatus.SUCCESS,
                    "Thanh toán tiền mặt"
            );
        }

        return toResponse(saved);
    }
}


