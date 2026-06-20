package org.mss301.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.shopservice.client.PaymentServiceClient;
import org.mss301.shopservice.client.dto.CreatePaymentRequest;
import org.mss301.shopservice.client.dto.PaymentResponse;
import org.mss301.shopservice.dto.request.PaymentResultRequest;
import org.mss301.shopservice.dto.request.SubscribeRequest;
import org.mss301.shopservice.dto.response.SubscribeResponse;
import org.mss301.shopservice.entity.Shop;
import org.mss301.shopservice.entity.ShopSubscription;
import org.mss301.shopservice.entity.SubscriptionPlan;
import org.mss301.shopservice.entity.SubscriptionTransaction;
import org.mss301.shopservice.entity.enumeration.BillingCycle;
import org.mss301.shopservice.entity.enumeration.ShopSubscriptionStatus;
import org.mss301.shopservice.entity.enumeration.SubscriptionPlanStatus;
import org.mss301.shopservice.entity.enumeration.SubscriptionTransactionStatus;
import org.mss301.shopservice.repository.ShopRepository;
import org.mss301.shopservice.repository.ShopSubscriptionRepository;
import org.mss301.shopservice.repository.SubscriptionPlanRepository;
import org.mss301.shopservice.repository.SubscriptionTransactionRepository;
import org.mss301.shopservice.service.SubscriptionPurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionPurchaseServiceImpl implements SubscriptionPurchaseService {

    private static final String GATEWAY_MOCK = "MOCK";
    private static final String REFERENCE_SUBSCRIPTION = "SUBSCRIPTION";

    private final SubscriptionPlanRepository planRepository;
    private final ShopSubscriptionRepository shopSubscriptionRepository;
    private final SubscriptionTransactionRepository transactionRepository;
    private final ShopRepository shopRepository;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    @Transactional
    public SubscribeResponse checkout(SubscribeRequest request) {
        SubscriptionPlan plan = planRepository.findById(request.getSubscriptionPlanId())
                .orElseThrow(() -> new BusinessException("Gói dịch vụ không tồn tại"));

        if (plan.getSubscriptionPlanStatus() != SubscriptionPlanStatus.ACTIVE) {
            throw new BusinessException("Gói dịch vụ không khả dụng");
        }

        long price = resolvePrice(plan, request.getBillingCycle());

        // 1. Tạo bản ghi subscription + transaction ở trạng thái PENDING
        ShopSubscription subscription = ShopSubscription.builder()
                .shopId(request.getShopId())
                .plan(plan)
                .billingCycle(request.getBillingCycle())
                .price(price)
                .autoRenewal(Boolean.TRUE.equals(request.getAutoRenewal()))
                .status(ShopSubscriptionStatus.PENDING)
                .build();
        subscription = shopSubscriptionRepository.save(subscription);

        SubscriptionTransaction transaction = SubscriptionTransaction.builder()
                .shopSubscription(subscription)
                .shopId(request.getShopId())
                .amount(price)
                .billingCycle(request.getBillingCycle())
                .paymentGateway(GATEWAY_MOCK)
                .status(SubscriptionTransactionStatus.PENDING)
                .build();
        transaction = transactionRepository.save(transaction);

        // 2. Gọi payment-service tạo link thanh toán
        PaymentResponse payment = paymentServiceClient.createPayment(CreatePaymentRequest.builder()
                .amount(price)
                .description("Thanh toan goi " + plan.getSubscriptionPlanName())
                .referenceType(REFERENCE_SUBSCRIPTION)
                .referenceId(String.valueOf(transaction.getSubscriptionTransactionId()))
                .build());

        transaction.setPaymentOrderCode(payment.getOrderCode());
        transactionRepository.save(transaction);

        log.info("Checkout: shop {} mua gói {} (subId={}, txnId={}, orderCode={})",
                request.getShopId(), plan.getSubscriptionPlanName(),
                subscription.getShopSubscriptionId(), transaction.getSubscriptionTransactionId(),
                payment.getOrderCode());

        return SubscribeResponse.builder()
                .shopSubscriptionId(subscription.getShopSubscriptionId())
                .subscriptionTransactionId(transaction.getSubscriptionTransactionId())
                .status(ShopSubscriptionStatus.PENDING.name())
                .amount(price)
                .paymentOrderCode(payment.getOrderCode())
                .payUrl(payment.getPayUrl())
                .build();
    }

    @Override
    @Transactional
    public void handlePaymentResult(PaymentResultRequest request) {
        Long transactionId = parseTransactionId(request.getReferenceId());

        SubscriptionTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Giao dịch không tồn tại"));

        // Idempotent: đã xử lý thành công rồi thì bỏ qua
        if (transaction.getStatus() == SubscriptionTransactionStatus.SUCCESS) {
            return;
        }

        ShopSubscription subscription = transaction.getShopSubscription();

        if (!request.isSuccess()) {
            transaction.setStatus(SubscriptionTransactionStatus.FAILED);
            subscription.setStatus(ShopSubscriptionStatus.CANCELLED);
            transactionRepository.save(transaction);
            shopSubscriptionRepository.save(subscription);
            log.info("Thanh toán thất bại cho txn {}", transactionId);
            return;
        }

        // 3. Thanh toán thành công → kích hoạt subscription
        LocalDateTime endedAt = computeEndDate(subscription.getBillingCycle());
        transaction.setStatus(SubscriptionTransactionStatus.SUCCESS);
        subscription.setStatus(ShopSubscriptionStatus.ACTIVE);
        subscription.setEndedAt(endedAt);
        transactionRepository.save(transaction);
        shopSubscriptionRepository.save(subscription);

        // 4. Cập nhật trực tiếp Shop trong cùng DB (không cần Feign call sang chính mình)
        shopRepository.findById(subscription.getShopId()).ifPresent(shop -> {
            shop.setCurrentPlanId(subscription.getPlan().getSubscriptionPlanId());
            shop.setCurrentPlanName(subscription.getPlan().getSubscriptionPlanName());
            shop.setSubscriptionStatus(ShopSubscriptionStatus.ACTIVE.name());
            shop.setSubscriptionEndedAt(endedAt);
            shop.setUpdatedAt(LocalDateTime.now());
            shopRepository.save(shop);
            log.info("Đã cập nhật gói pro cho shop {}", subscription.getShopId());
        });
    }

    // ---------- helpers ----------

    private long resolvePrice(SubscriptionPlan plan, BillingCycle cycle) {
        Long price = (cycle == BillingCycle.YEARLY) ? plan.getPriceYearly() : plan.getPriceMonthly();
        if (price == null) {
            throw new BusinessException("Gói dịch vụ chưa cấu hình giá cho chu kỳ này");
        }
        return price;
    }

    private LocalDateTime computeEndDate(BillingCycle cycle) {
        LocalDateTime now = LocalDateTime.now();
        return (cycle == BillingCycle.YEARLY) ? now.plusYears(1) : now.plusMonths(1);
    }

    private Long parseTransactionId(String referenceId) {
        try {
            return Long.valueOf(referenceId);
        } catch (NumberFormatException e) {
            throw new BusinessException("referenceId không hợp lệ");
        }
    }
}
