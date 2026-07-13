# Đánh giá Phase 2 & Code gợi ý Phase 3 (MoMo)

---

## ✅ Đánh giá Phase 2: `order-service`

### Kết luận: **Phase 2 đúng và đầy đủ** — tất cả checklist đã đáp ứng.

| Checklist | File | Trạng thái | Ghi chú |
|-----------|------|-----------|---------|
| Entity `Order`, `OrderItem`, `OrderStatus` | `entity/Order.java`, enums | ✅ | Đầy đủ, có `PaymentGateway` enum với `MOMO`, `VNPAY`, `CASH` |
| Kafka Producer: publish `order.created` | `OrderEventProducer.java` | ✅ | Map sang `OrderCreatedEvent` trước khi gửi, tránh lazy-load issue |
| Kafka Consumer: listen `payment.status` | `SagaEventConsumer.java` | ✅ | Xử lý SUCCESS/FAIL, update OrderStatus |
| Kafka Consumer: listen `inventory.status` | `SagaEventConsumer.java` | ✅ | Update PAID hoặc CANCELLED |
| Saga state machine | `SagaEventConsumer.java` | ✅ | Choreography-based, đúng pattern |
| Shared event DTOs | `common-service/dto/event` | ✅ | `OrderCreatedEvent`, `PaymentStatusEvent`, `InventoryStatusEvent` |
| KafkaConfig (Producer + Consumer) | `common-service/KafkaConfig.java` | ✅ | Có `ErrorHandlingDeserializer` chống Poison Pill |
| Topic declaration | `KafkaTopicConfig.java` | ✅ | 3 topics, 3 partitions, 1 replica |

### Một điểm nhỏ cần chú ý

```java
// Order.java, line 75 — có bug type mismatch
if (status == null) status = OrderStepStatus.PENDING;  // ❌ sai type!
// Sửa lại thành:
if (status == null) status = OrderStatus.PENDING;       // ✅
```

> `OrderStepStatus` là enum của Kafka event (common-service), không phải `OrderStatus` của entity. Build sẽ lỗi hoặc warning.

---

## 🚀 Phase 3: Refactor `payment-service` + Tích hợp MoMo

### Tổng quan những gì cần làm

```
payment-service hiện tại (synchronous):
  POST /api/payments → tạo order, trả về mock payUrl
  POST /api/payments/{code}/confirm → mark PAID, Feign callback → subscription-service

payment-service sau Phase 3 (async + MoMo):
  [Kafka IN]  order.created  → tạo MoMo payment link → gửi link qua Kafka
  [MoMo IPN]  POST /momo/ipn → MoMo server gọi vào → verify → publish payment.status
  [Kafka OUT] payment.status → order-service nhận kết quả
```

---

## 📁 Các file cần tạo/sửa trong `payment-service`

### 1. `pom.xml` — Thêm dependency Kafka + HTTP Client

```xml
<!-- Thêm vào <dependencies> -->

<!-- Spring Kafka: để consume order.created và produce payment.status -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<!-- OkHttp hoặc RestClient để gọi MoMo API (Spring Boot 3.x dùng RestClient) -->
<!-- Không cần thêm dependency vì Spring Boot Web đã có RestClient -->
```

---

### 2. `application.yml` — Thêm config Kafka + MoMo

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    consumer:
      group-id: payment-service-group
      auto-offset-reset: earliest

# ---- MoMo Sandbox Configuration ----
# Lấy credentials từ: https://developers.momo.vn/v3/docs/payment/onboarding/test-instructions
momo:
  partner-code: ${MOMO_PARTNER_CODE}       # ví dụ: MOMOBKUN20180529
  access-key: ${MOMO_ACCESS_KEY}           # Access Key từ MoMo
  secret-key: ${MOMO_SECRET_KEY}           # Secret Key từ MoMo
  endpoint: ${MOMO_ENDPOINT:https://test-payment.momo.vn/v2/gateway/api/create}
  ipn-url: ${MOMO_IPN_URL}                 # URL public để MoMo callback vào (dùng ngrok khi dev)
  redirect-url: ${MOMO_REDIRECT_URL}       # URL redirect sau khi user thanh toán xong
```

---

### 3. `MomoProperties.java` — Config bean cho MoMo credentials

```java
package org.mss301.paymentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Bind tất cả config "momo.*" từ application.yml vào bean này.
 * Dùng @ConfigurationProperties thay vì @Value để dễ test và type-safe.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "momo")
public class MomoProperties {
    /** Partner Code do MoMo cấp khi đăng ký merchant */
    private String partnerCode;

    /** Access Key dùng để định danh merchant trong request */
    private String accessKey;

    /** Secret Key dùng để tạo chữ ký HMAC-SHA256 */
    private String secretKey;

    /** MoMo API endpoint (sandbox hoặc production) */
    private String endpoint;

    /**
     * IPN URL: địa chỉ MoMo sẽ POST kết quả thanh toán về.
     * Phải là URL public có thể reach được từ internet.
     * Khi dev local, dùng ngrok: ngrok http 8085
     */
    private String ipnUrl;

    /**
     * Redirect URL: trang web sẽ redirect user sau khi họ hoàn tất thanh toán trên app MoMo.
     * Thường là trang "Cảm ơn đơn hàng #XXX đã được thanh toán"
     */
    private String redirectUrl;
}
```

---

### 4. `MomoSignatureUtil.java` — Tạo & verify chữ ký HMAC-SHA256

```java
package org.mss301.paymentservice.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Utility tạo và xác thực chữ ký HMAC-SHA256 cho MoMo API.
 *
 * MoMo yêu cầu tất cả request đều phải ký bằng HMAC-SHA256
 * để đảm bảo integrity và authenticity của dữ liệu.
 *
 * Tài liệu: https://developers.momo.vn/v3/docs/payment/api/payment-api
 */
public class MomoSignatureUtil {

    /**
     * Tạo chữ ký HMAC-SHA256 từ raw data và secret key.
     *
     * @param data      Chuỗi cần ký (thường là chuỗi query string các field theo thứ tự alphabet)
     * @param secretKey Secret key do MoMo cấp
     * @return Chữ ký dạng hex lowercase
     */
    public static String hmacSHA256(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Chuyển byte array sang hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo chữ ký HMAC-SHA256", e);
        }
    }

    /**
     * Verify chữ ký từ IPN request của MoMo.
     * So sánh chữ ký MoMo gửi về với chữ ký ta tự tính để xác nhận request hợp lệ.
     *
     * @param rawData   Chuỗi dữ liệu gốc để ký (build từ các field trong IPN request)
     * @param secretKey Secret key của merchant
     * @param signature Chữ ký MoMo gửi về trong IPN
     * @return true nếu signature hợp lệ
     */
    public static boolean verifySignature(String rawData, String secretKey, String signature) {
        String computed = hmacSHA256(rawData, secretKey);
        return computed.equals(signature);
    }
}
```

---

### 5. `MomoApiClient.java` — Gọi MoMo API tạo payment link

```java
package org.mss301.paymentservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.paymentservice.config.MomoProperties;
import org.mss301.paymentservice.dto.momo.MomoPaymentRequest;
import org.mss301.paymentservice.dto.momo.MomoPaymentResponse;
import org.mss301.paymentservice.util.MomoSignatureUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

/**
 * Client gọi MoMo Payment Gateway API.
 *
 * Flow:
 *   1. Nhận thông tin order từ Kafka event
 *   2. Build request body theo format MoMo yêu cầu
 *   3. Ký request bằng HMAC-SHA256
 *   4. POST lên MoMo API → nhận payUrl
 *   5. Trả payUrl về để gửi cho user
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MomoApiClient {

    private final MomoProperties momoProperties;

    // Spring Boot 3.x có sẵn RestClient (thay thế RestTemplate)
    private final RestClient restClient = RestClient.create();

    /**
     * Tạo payment link MoMo cho một order.
     *
     * @param orderId     ID của order (từ order-service)
     * @param orderCode   Mã order (dùng làm orderId gửi lên MoMo)
     * @param amount      Số tiền (đơn vị: VNĐ, phải là số nguyên)
     * @param description Mô tả đơn hàng hiển thị trên app MoMo
     * @return MoMo payment link để redirect user, hoặc null nếu lỗi
     */
    public String createPaymentLink(Long orderId, String orderCode, Long amount, String description) {
        // requestId: mỗi request phải có requestId unique để idempotency
        String requestId = UUID.randomUUID().toString();

        /*
         * Build raw signature string theo đúng format MoMo yêu cầu:
         * Sắp xếp các field theo alphabet và nối bằng dấu &
         * accessKey=...&amount=...&extraData=...&ipnUrl=...&orderId=...
         * &orderInfo=...&partnerCode=...&redirectUrl=...&requestId=...
         * &requestType=payWithATM
         *
         * Lưu ý: KHÔNG bao gồm field "signature" trong raw data
         */
        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData="
                + "&ipnUrl=" + momoProperties.getIpnUrl()
                + "&orderId=" + orderCode          // MoMo dùng orderId là string unique
                + "&orderInfo=" + description
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + momoProperties.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=payWithATM";        // hoặc "captureWallet" cho QR/ví MoMo

        // Ký request
        String signature = MomoSignatureUtil.hmacSHA256(rawSignature, momoProperties.getSecretKey());

        // Build request body
        MomoPaymentRequest request = MomoPaymentRequest.builder()
                .partnerCode(momoProperties.getPartnerCode())
                .requestId(requestId)
                .amount(amount)
                .orderId(orderCode)
                .orderInfo(description)
                .redirectUrl(momoProperties.getRedirectUrl())
                .ipnUrl(momoProperties.getIpnUrl())
                .requestType("payWithATM")
                .extraData("")                     // Base64 encoded, để trống nếu không dùng
                .lang("vi")
                .signature(signature)
                .build();

        log.info("[MoMo] Đang tạo payment link cho orderId={}, amount={}", orderCode, amount);

        try {
            // Gọi MoMo API
            MomoPaymentResponse response = restClient.post()
                    .uri(momoProperties.getEndpoint())
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(MomoPaymentResponse.class);

            if (response != null && response.getResultCode() == 0) {
                // resultCode = 0 là thành công
                log.info("[MoMo] Tạo link thành công cho orderId={}: {}", orderCode, response.getPayUrl());
                return response.getPayUrl();
            } else {
                log.error("[MoMo] Tạo link thất bại cho orderId={}: resultCode={}, message={}",
                        orderCode,
                        response != null ? response.getResultCode() : "null",
                        response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("[MoMo] Lỗi kết nối MoMo API cho orderId={}", orderCode, e);
            return null;
        }
    }
}
```

---

### 6. DTOs cho MoMo API

#### `MomoPaymentRequest.java`

```java
package org.mss301.paymentservice.dto.momo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body gửi lên MoMo API để tạo payment link.
 * Tham khảo: https://developers.momo.vn/v3/docs/payment/api/payment-api/#parameters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentRequest {

    /** Mã merchant do MoMo cấp */
    private String partnerCode;

    /** ID request unique, dùng để idempotency */
    private String requestId;

    /** Số tiền thanh toán (VNĐ, phải là số nguyên dương) */
    private Long amount;

    /** Mã đơn hàng unique trong hệ thống của bạn */
    private String orderId;

    /** Mô tả đơn hàng hiển thị trên app MoMo */
    private String orderInfo;

    /** URL redirect sau khi user thanh toán xong */
    private String redirectUrl;

    /** URL MoMo sẽ POST kết quả về (IPN = Instant Payment Notification) */
    private String ipnUrl;

    /**
     * Loại thanh toán:
     * - "captureWallet": quét QR / thanh toán bằng ví MoMo
     * - "payWithATM": thanh toán bằng thẻ ATM/ngân hàng
     * - "payWithCC": thẻ tín dụng
     */
    private String requestType;

    /**
     * Data bổ sung, encode base64. Để trống nếu không cần.
     * Sẽ được gửi lại trong IPN để trace về đơn hàng gốc.
     */
    private String extraData;

    /** Ngôn ngữ hiển thị: "vi" hoặc "en" */
    private String lang;

    /** Chữ ký HMAC-SHA256 */
    private String signature;
}
```

#### `MomoPaymentResponse.java`

```java
package org.mss301.paymentservice.dto.momo;

import lombok.Data;

/**
 * Response từ MoMo API sau khi tạo payment link.
 * resultCode = 0 → thành công
 * resultCode != 0 → xem message để biết lỗi gì
 */
@Data
public class MomoPaymentResponse {

    /** Mã merchant */
    private String partnerCode;

    /** ID request (echo lại từ request) */
    private String requestId;

    /** Mã đơn hàng (echo lại) */
    private String orderId;

    /**
     * Kết quả:
     * 0 = thành công
     * Khác 0 = thất bại (xem docs MoMo để biết mã lỗi)
     */
    private int resultCode;

    /** Thông báo kết quả */
    private String message;

    /**
     * URL thanh toán MoMo — redirect user vào đây hoặc tạo QR code.
     * Chỉ có giá trị khi resultCode = 0
     */
    private String payUrl;

    /** Deep link để mở thẳng app MoMo (optional) */
    private String deeplink;

    /** QR code image URL (optional) */
    private String qrCodeUrl;
}
```

#### `MomoIpnRequest.java`

```java
package org.mss301.paymentservice.dto.momo;

import lombok.Data;

/**
 * IPN (Instant Payment Notification) request từ MoMo server gọi vào hệ thống.
 *
 * MoMo sẽ POST request này vào ipnUrl sau khi user thanh toán.
 * PHẢI verify signature trước khi tin tưởng dữ liệu này.
 *
 * Lưu ý: MoMo có thể gửi IPN nhiều lần → cần xử lý idempotent.
 */
@Data
public class MomoIpnRequest {
    private String partnerCode;
    private String orderId;         // Mã đơn hàng của bạn (đã gửi lên khi tạo)
    private String requestId;
    private Long amount;
    private String orderInfo;
    private String orderType;
    private Long transId;           // MoMo transaction ID
    private int resultCode;         // 0 = thành công, 9000 = user đang xử lý
    private String message;
    private String payType;         // "qr", "webApp", "credit", "debitCard"
    private Long responseTime;      // Timestamp milliseconds
    private String extraData;       // Data bổ sung đã gửi lên
    private String signature;       // Chữ ký MoMo → BẮT BUỘC verify
}
```

---

### 7. `OrderCreatedConsumer.java` — Kafka Consumer nhận `order.created`

```java
package org.mss301.paymentservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.OrderCreatedEvent;
import org.mss301.paymentservice.client.MomoApiClient;
import org.mss301.paymentservice.entity.PaymentOrder;
import org.mss301.paymentservice.entity.enumeration.PaymentStatus;
import org.mss301.paymentservice.repository.PaymentOrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka Consumer lắng nghe topic "order.created" từ order-service.
 *
 * Khi nhận được event:
 *   1. Lưu PaymentOrder vào DB với status PENDING
 *   2. Nếu paymentGateway = MOMO → gọi MoMo API tạo payment link
 *   3. Lưu payUrl vào DB
 *   (Sau này: gửi payUrl về order-service qua Kafka để forward cho client)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final PaymentOrderRepository paymentOrderRepository;
    private final MomoApiClient momoApiClient;
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(
        topics = "order.created",
        groupId = "payment-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[Kafka] Nhận 'order.created': orderId={}, gateway={}, amount={}",
                event.getOrderId(), event.getPaymentGateway(), event.getPaidPrice());

        // Kiểm tra idempotency: nếu đã có PaymentOrder cho orderId này thì bỏ qua
        // Dùng orderId Long thay vì referenceId String để type-safe
        if (paymentOrderRepository.existsByOrderId(event.getOrderId())) {
            log.warn("[Kafka] Đã tồn tại PaymentOrder cho orderId={}, bỏ qua", event.getOrderId());
            return;
        }

        // Chuyển float sang long (MoMo yêu cầu số nguyên, đơn vị VNĐ)
        Long amount = event.getPaidPrice() != null
                ? Math.round(event.getPaidPrice())
                : Math.round(event.getBasePrice());

        // Tạo và lưu PaymentOrder trạng thái PENDING
        PaymentOrder paymentOrder = PaymentOrder.builder()
                .orderCode("PAY-" + event.getOrderCode())
                .amount(amount)
                .description("Thanh toán đơn hàng " + event.getOrderCode())
                .referenceType("ORDER")          // Phân biệt với SUBSCRIPTION
                .referenceId(String.valueOf(event.getOrderId()))  // giữ String cho tương thích
                .orderId(event.getOrderId())      // Long — dùng để query nhanh sau này
                .status(PaymentStatus.PENDING)
                .build();

        PaymentOrder saved = paymentOrderRepository.save(paymentOrder);
        log.info("[DB] Tạo PaymentOrder id={} cho orderId={}", saved.getPaymentOrderId(), event.getOrderId());

        // Xử lý theo payment gateway
        String gateway = event.getPaymentGateway();

        if ("MOMO".equals(gateway)) {
            handleMomoPayment(event, saved, amount);
        } else if ("VNPAY".equals(gateway)) {
            // TODO Phase 3.x: Tích hợp VNPay tương tự
            log.info("[VNPay] Chưa tích hợp, bỏ qua orderId={}", event.getOrderId());
        } else if ("CASH".equals(gateway)) {
            // Thanh toán tiền mặt: không cần gateway, confirm ngay
            paymentEventProducer.publishPaymentStatus(
                event.getOrderId(), saved.getPaymentOrderId(), event.getShopId(),
                org.mss301.commonservice.dto.event.enumeration.OrderStepStatus.SUCCESS,
                "Thanh toán tiền mặt"
            );
        }
    }

    /**
     * Gọi MoMo API tạo payment link và lưu payUrl vào DB.
     * payUrl sẽ được dùng để hiển thị QR code hoặc redirect user.
     */
    private void handleMomoPayment(OrderCreatedEvent event, PaymentOrder paymentOrder, Long amount) {
        String payUrl = momoApiClient.createPaymentLink(
                event.getOrderId(),
                paymentOrder.getOrderCode(),
                amount,
                paymentOrder.getDescription()
        );

        if (payUrl != null) {
            // Lưu payUrl vào DB để có thể retrieve sau
            paymentOrder.setPayUrl(payUrl);
            paymentOrderRepository.save(paymentOrder);
            log.info("[MoMo] Đã lưu payUrl cho orderId={}", event.getOrderId());
            // TODO: Publish event "payment.initiated" với payUrl để order-service / client biết
        } else {
            // Tạo link thất bại → publish payment.status = FAILED về order-service
            log.error("[MoMo] Tạo link thất bại → publish payment.status=CANCELLED cho orderId={}", event.getOrderId());
            paymentEventProducer.publishPaymentStatus(
                event.getOrderId(), paymentOrder.getPaymentOrderId(), event.getShopId(),
                org.mss301.commonservice.dto.event.enumeration.OrderStepStatus.CANCELLED,
                "Không thể tạo link thanh toán MoMo"
            );
        }
    }
}
```

---

### 8. `PaymentEventProducer.java` — Kafka Producer publish `payment.status`

```java
package org.mss301.paymentservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.PaymentStatusEvent;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka Producer publish kết quả thanh toán về order-service.
 *
 * Topic: "payment.status"
 * Consumer: SagaEventConsumer trong order-service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Tên topic phải khớp với @KafkaListener trong order-service
    public static final String TOPIC_PAYMENT_STATUS = "payment.status";

    /**
     * Publish kết quả thanh toán lên Kafka.
     *
     * @param orderId       ID của order (để order-service tìm đúng record)
     * @param transactionId ID của PaymentOrder trong payment-service
     * @param shopId        ID của shop (để routing / audit)
     * @param status        SUCCESS hoặc CANCELLED
     * @param message       Thông báo mô tả (lý do thất bại, tên ngân hàng, v.v.)
     */
    public void publishPaymentStatus(
            Long orderId, Long transactionId, Long shopId,
            OrderStepStatus status, String message) {

        PaymentStatusEvent event = PaymentStatusEvent.builder()
                .orderId(orderId)
                .transactionId(transactionId)
                .shopId(shopId)
                .status(status)
                .message(message)
                .build();

        // Key = orderId để đảm bảo event của cùng 1 order vào cùng 1 partition → ordered
        kafkaTemplate.send(TOPIC_PAYMENT_STATUS, String.valueOf(orderId), event);
        log.info("[Kafka] Published '{}': orderId={}, status={}", TOPIC_PAYMENT_STATUS, orderId, status);
    }
}
```

---

### 9. `MomoIpnController.java` — Nhận IPN callback từ MoMo

```java
package org.mss301.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.mss301.paymentservice.config.MomoProperties;
import org.mss301.paymentservice.dto.momo.MomoIpnRequest;
import org.mss301.paymentservice.entity.PaymentOrder;
import org.mss301.paymentservice.entity.enumeration.PaymentStatus;
import org.mss301.paymentservice.event.PaymentEventProducer;
import org.mss301.paymentservice.repository.PaymentOrderRepository;
import org.mss301.paymentservice.util.MomoSignatureUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller nhận IPN (Instant Payment Notification) từ MoMo server.
 *
 * QUAN TRỌNG:
 * - Endpoint này PHẢI public (không cần JWT token)
 * - PHẢI verify signature trước khi xử lý
 * - PHẢI idempotent (MoMo có thể gọi nhiều lần)
 * - PHẢI response trong vòng 30 giây, nếu không MoMo sẽ retry
 * - PHẢI return HTTP 200 dù thành công hay thất bại
 */
@Slf4j
@RestController
@RequestMapping("/momo")
@RequiredArgsConstructor
public class MomoIpnController {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final MomoProperties momoProperties;

    /**
     * MoMo POST vào đây sau khi user thanh toán.
     *
     * URL ví dụ: POST https://your-domain.com/momo/ipn
     * (trong dev dùng ngrok: POST https://xxxx.ngrok.io/momo/ipn)
     */
    @PostMapping("/ipn")
    public ResponseEntity<Map<String, String>> handleIpn(@RequestBody MomoIpnRequest ipnRequest) {
        log.info("[MoMo IPN] Nhận callback: orderId={}, resultCode={}, transId={}",
                ipnRequest.getOrderId(), ipnRequest.getResultCode(), ipnRequest.getTransId());

        // ===== BƯỚC 1: Verify chữ ký để đảm bảo request đến từ MoMo (không phải fake) =====
        /*
         * Raw signature string phải được build theo đúng format MoMo quy định cho IPN:
         * Các field và thứ tự KHÁC với lúc tạo payment → xem tài liệu MoMo
         */
        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + ipnRequest.getAmount()
                + "&extraData=" + ipnRequest.getExtraData()
                + "&message=" + ipnRequest.getMessage()
                + "&orderId=" + ipnRequest.getOrderId()
                + "&orderInfo=" + ipnRequest.getOrderInfo()
                + "&orderType=" + ipnRequest.getOrderType()
                + "&partnerCode=" + ipnRequest.getPartnerCode()
                + "&payType=" + ipnRequest.getPayType()
                + "&requestId=" + ipnRequest.getRequestId()
                + "&responseTime=" + ipnRequest.getResponseTime()
                + "&resultCode=" + ipnRequest.getResultCode()
                + "&transId=" + ipnRequest.getTransId();

        boolean isValid = MomoSignatureUtil.verifySignature(
                rawSignature, momoProperties.getSecretKey(), ipnRequest.getSignature());

        if (!isValid) {
            // Chữ ký sai → có thể bị tấn công, log và từ chối
            log.error("[MoMo IPN] ⚠️ INVALID SIGNATURE cho orderId={} — Bỏ qua!",
                    ipnRequest.getOrderId());
            // Vẫn return 200 để MoMo không retry, nhưng không xử lý gì cả
            return ResponseEntity.ok(Map.of("status", "INVALID_SIGNATURE"));
        }

        // ===== BƯỚC 2: Tìm PaymentOrder trong DB =====
        PaymentOrder paymentOrder = paymentOrderRepository
                .findByOrderCode(ipnRequest.getOrderId())
                .orElse(null);

        if (paymentOrder == null) {
            log.error("[MoMo IPN] Không tìm thấy PaymentOrder với code={}", ipnRequest.getOrderId());
            return ResponseEntity.ok(Map.of("status", "NOT_FOUND"));
        }

        // ===== BƯỚC 3: Idempotency check — nếu đã xử lý rồi thì bỏ qua =====
        if (paymentOrder.getStatus() == PaymentStatus.PAID
                || paymentOrder.getStatus() == PaymentStatus.FAILED) {
            log.warn("[MoMo IPN] Đã xử lý rồi (status={}), bỏ qua orderId={}",
                    paymentOrder.getStatus(), ipnRequest.getOrderId());
            return ResponseEntity.ok(Map.of("status", "ALREADY_PROCESSED"));
        }

        // ===== BƯỚC 4: Xử lý kết quả thanh toán =====
        // Lấy orderId gốc từ referenceId (đã lưu khi tạo PaymentOrder)
        Long orderId = Long.valueOf(paymentOrder.getReferenceId());

        if (ipnRequest.getResultCode() == 0) {
            // ✅ Thanh toán thành công
            paymentOrder.setStatus(PaymentStatus.PAID);
            paymentOrder.setPaidAt(LocalDateTime.now());
            paymentOrderRepository.save(paymentOrder);

            log.info("[MoMo IPN] ✅ Thanh toán thành công: orderId={}, transId={}",
                    ipnRequest.getOrderId(), ipnRequest.getTransId());

            // Publish kết quả về order-service qua Kafka
            paymentEventProducer.publishPaymentStatus(
                    orderId,
                    paymentOrder.getPaymentOrderId(),
                    null, // shopId có thể lấy từ extraData nếu cần
                    OrderStepStatus.SUCCESS,
                    "Thanh toán MoMo thành công. TransId: " + ipnRequest.getTransId()
            );

        } else {
            // ❌ Thanh toán thất bại (user hủy, hết thời gian, lỗi ngân hàng...)
            paymentOrder.setStatus(PaymentStatus.FAILED);
            paymentOrderRepository.save(paymentOrder);

            log.warn("[MoMo IPN] ❌ Thanh toán thất bại: orderId={}, resultCode={}, message={}",
                    ipnRequest.getOrderId(), ipnRequest.getResultCode(), ipnRequest.getMessage());

            paymentEventProducer.publishPaymentStatus(
                    orderId,
                    paymentOrder.getPaymentOrderId(),
                    null,
                    OrderStepStatus.CANCELLED,
                    "Thanh toán MoMo thất bại: " + ipnRequest.getMessage()
            );
        }

        // PHẢI return 200 để MoMo biết đã nhận IPN thành công
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
}
```

---

### 10. Cập nhật `PaymentOrder.java` entity — Thêm `payUrl` và `FAILED` status

```java
// Thêm field payUrl vào entity (để lưu link MoMo)
@Column(name = "pay_url", length = 1000)
String payUrl;

// Thêm field momoTransId để audit
@Column(name = "momo_trans_id")
Long momoTransId;
```

```java
// Thêm FAILED vào PaymentStatus enum
public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED   // ← thêm mới cho trường hợp MoMo trả về lỗi
}
```

---

### 11. `SecurityConfig` trong `payment-service` — Mở public IPN endpoint

```java
// Trong SecurityConfig (hoặc tạo mới nếu chưa có):
// Cho phép MoMo server gọi vào /momo/ipn mà không cần JWT

.requestMatchers("/momo/ipn").permitAll()   // ← thêm dòng này
```

---

## 🗂️ Tóm tắt cấu trúc file Phase 3

```
payment-service/src/main/java/org/mss301/paymentservice/
├── config/
│   └── MomoProperties.java          [NEW] Bind config momo.*
├── util/
│   └── MomoSignatureUtil.java       [NEW] HMAC-SHA256 sign & verify
├── client/
│   ├── SubscriptionCallbackClient   [giữ nguyên]
│   └── MomoApiClient.java           [NEW] Gọi MoMo API tạo link
├── dto/
│   ├── momo/
│   │   ├── MomoPaymentRequest.java  [NEW]
│   │   ├── MomoPaymentResponse.java [NEW]
│   │   └── MomoIpnRequest.java      [NEW]
│   ├── request/
│   │   └── CreatePaymentRequest     [giữ nguyên]
│   └── response/
│       └── PaymentResponse          [giữ nguyên]
├── event/
│   ├── OrderCreatedConsumer.java    [NEW] Listen order.created
│   └── PaymentEventProducer.java    [NEW] Publish payment.status
├── controller/
│   ├── PaymentController            [giữ nguyên - subscription flow]
│   └── MomoIpnController.java       [NEW] Nhận IPN từ MoMo
└── entity/
    ├── PaymentOrder                 [MODIFY] thêm payUrl, momoTransId
    └── enumeration/
        └── PaymentStatus            [MODIFY] thêm FAILED
```

---

## ⚠️ Checklist trước khi chạy Phase 3

- [ ] Thêm Kafka dependency vào `payment-service/pom.xml`
- [ ] Thêm `@EnableConfigurationProperties(MomoProperties.class)` vào main class hoặc config
- [ ] Thêm `MOMO_PARTNER_CODE`, `MOMO_ACCESS_KEY`, `MOMO_SECRET_KEY` vào `.env`
- [ ] Dùng **ngrok** để expose port 8085 → lấy URL điền vào `MOMO_IPN_URL`
- [ ] Kiểm tra `SecurityConfig`: cho phép `/momo/ipn` bypass JWT
- [ ] Fix bug `Order.java` line 75: đổi `OrderStepStatus.PENDING` → `OrderStatus.PENDING`
- [ ] Thêm `existsByReferenceId()` vào `PaymentOrderRepository`
