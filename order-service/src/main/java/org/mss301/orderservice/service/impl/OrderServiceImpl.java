package org.mss301.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.commonservice.dto.response.ApiResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.orderservice.client.CatalogServiceClient;
import org.mss301.orderservice.client.PaymentServiceClient;
import org.mss301.orderservice.dto.request.CreateOrderPaymentRequest;
import org.mss301.orderservice.dto.request.OrderItemRequest;
import org.mss301.orderservice.dto.request.OrderRequest;
import org.mss301.orderservice.dto.request.ToppingItemRequest;
import org.mss301.orderservice.dto.response.OrderResponse;
import org.mss301.orderservice.entity.Order;
import org.mss301.orderservice.entity.OrderItem;
import org.mss301.orderservice.entity.Promotion;
import org.mss301.orderservice.entity.PromotionTarget;
import org.mss301.orderservice.entity.PromotionUsage;
import org.mss301.orderservice.entity.ToppingPerOrderItem;
import org.mss301.orderservice.entity.enumeration.DiscountType;
import org.mss301.orderservice.entity.enumeration.OrderItemStatus;
import org.mss301.orderservice.entity.enumeration.OrderStatus;
import org.mss301.orderservice.entity.enumeration.PromotionStatus;
import org.mss301.orderservice.entity.enumeration.PromotionType;
import org.mss301.orderservice.entity.enumeration.PromotionUsageStatus;
import org.mss301.orderservice.entity.enumeration.ToppingPerOrderItemStatus;
import org.mss301.orderservice.event.OrderEventProducer;
import org.mss301.orderservice.mapper.OrderMapper;
import org.mss301.orderservice.repository.OrderRepository;
import org.mss301.orderservice.repository.PromotionRepository;
import org.mss301.orderservice.repository.PromotionTargetRepository;
import org.mss301.orderservice.repository.PromotionUsageRepository;
import org.mss301.orderservice.service.inter.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final CatalogServiceClient catalogServiceClient;
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final PromotionTargetRepository promotionTargetRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderMapper orderMapper;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // 0. Đăng ký chờ payUrl TRƯỚC (nếu là gateway online) — tránh race condition
        //    CompletableFuture sẵn sàng nhận reply từ payment-service
        Order savedOrder = transactionTemplate.execute(status -> {
            Long shopId = TenantContext.getCurrentShopId();
            if (shopId == null) {
                throw new BusinessException("ShopId không tìm thấy trong context");
            }
            log.info("Bắt đầu tạo đơn hàng mới cho khách hàng: {}, shopId: {}", request.getCustomerId(), shopId);

            List<OrderItem> orderItems = new ArrayList<>();
            long calculatedBasePrice = 0L;
            Map<Long, Long> variantToProductMap = new HashMap<>();

            // 1. Map các OrderItem từ request và lấy chi tiết từ catalog-service
            for (OrderItemRequest itemRequest : request.getOrderItems()) {
                ApiResponse<Map<String, Object>> variantResponse;
                try {
                    variantResponse = catalogServiceClient.getProductVariantById(itemRequest.getProductVariantId());
                } catch (Exception e) {
                    throw new BusinessException("Không thể lấy thông tin sản phẩm từ hệ thống");
                }

                if (variantResponse == null || variantResponse.getData() == null) {
                    throw new BusinessException(
                            "Sản phẩm variantId=" + itemRequest.getProductVariantId() + " không tồn tại");
                }

                Map<String, Object> variantData = variantResponse.getData();
                Long variantPrice = toLong(variantData.get("price"));
                if (variantPrice == null) variantPrice = 0L;
                Long productId = toLong(variantData.get("productId"));

                // Lưu mapping variantId -> productId để check promotion
                if (productId != null) {
                    variantToProductMap.put(itemRequest.getProductVariantId(), productId);
                }

                OrderItem orderItem = orderMapper.toEntity(itemRequest);
                orderItem.setUnitPrice(variantPrice);
                orderItem.setStatus(OrderItemStatus.PENDING);

                long itemTotalPrice = variantPrice * itemRequest.getQuantity();

                // 2. Map các Topping của từng OrderItem
                List<ToppingPerOrderItem> toppings = new ArrayList<>();
                if (itemRequest.getToppingItems() != null) {
                    for (ToppingItemRequest toppingRequest : itemRequest.getToppingItems()) {
                        ApiResponse<Map<String, Object>> toppingResponse;
                        try {
                            toppingResponse = catalogServiceClient.getToppingById(toppingRequest.getToppingId());
                        } catch (Exception e) {
                            throw new BusinessException("Không thể lấy thông tin topping từ hệ thống");
                        }

                        if (toppingResponse == null || toppingResponse.getData() == null) {
                            throw new BusinessException("Topping id=" + toppingRequest.getToppingId() + " không tồn tại");
                        }

                        Map<String, Object> toppingData = toppingResponse.getData();
                        Long toppingPrice = toLong(toppingData.get("price"));
                        if (toppingPrice == null) toppingPrice = 0L;

                        ToppingPerOrderItem toppingItem = orderMapper.toEntity(toppingRequest);
                        toppingItem.setOrderItem(orderItem);
                        toppingItem.setToppingName(toStringValue(toppingData.get("name")));
                        toppingItem.setUnitPrice(toppingPrice);
                        toppingItem.setStatus(ToppingPerOrderItemStatus.ACTIVE);

                        toppings.add(toppingItem);
                        itemTotalPrice += toppingPrice * toppingRequest.getQuantity() * itemRequest.getQuantity();
                    }
                    orderItem.setToppings(toppings);
                }

                calculatedBasePrice += itemTotalPrice;
                orderItems.add(orderItem);
            }

            long basePriceLong = calculatedBasePrice;
            long paidPriceLong = basePriceLong;
            long discountAmount = 0L;
            Promotion appliedPromotion = null;

            // 3. Xử lý Promotion nếu có promotionCode
            if (request.getPromotionCode() != null && !request.getPromotionCode().trim().isEmpty()) {
                String code = request.getPromotionCode().trim();
                var promotionOpt = promotionRepository.findByPromotionCodeAndShopId(code, shopId);
                if (promotionOpt.isEmpty()) {
                    throw new BusinessException("Mã khuyến mãi không hợp lệ hoặc không thuộc cửa hàng này");
                }

                Promotion promotion = promotionOpt.get();
                LocalDateTime now = LocalDateTime.now();

                if (promotion.getStatus() != PromotionStatus.ACTIVE) {
                    throw new BusinessException("Mã khuyến mãi hiện không hoạt động");
                }
                if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
                    throw new BusinessException("Mã khuyến mãi chưa đến thời gian áp dụng");
                }
                if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
                    throw new BusinessException("Mã khuyến mãi đã hết hạn");
                }
                if (promotion.getMinimumSpent() != null && basePriceLong < promotion.getMinimumSpent()) {
                    throw new BusinessException(
                            "Giá trị đơn hàng chưa đạt mức tối thiểu " + promotion.getMinimumSpent() + "đ để áp dụng mã");
                }
                if (promotion.getQuantity() != null && promotion.getQuantity() <= 0) {
                    throw new BusinessException("Mã khuyến mãi đã hết lượt sử dụng");
                }
                // Kiểm tra giới hạn sử dụng per user
                if (promotion.getUsageLimitPerUser() != null && promotion.getUsageLimitPerUser() > 0) {
                    long usedCount = promotionUsageRepository.countByPromotionIdAndCustomerId(
                            promotion.getPromotionId(), request.getCustomerId());
                    if (usedCount >= promotion.getUsageLimitPerUser()) {
                        throw new BusinessException("Bạn đã đạt giới hạn sử dụng mã khuyến mãi này");
                    }
                }

                if (promotion.getPromotionType() == PromotionType.PRODUCT) {
                    // Chỉ discount những sản phẩm nằm trong PromotionTarget
                    Set<Long> targetProductIds = promotionTargetRepository
                            .findByPromotion_PromotionId(promotion.getPromotionId())
                            .stream()
                            .map(PromotionTarget::getProductId)
                            .collect(Collectors.toSet());

                    long targetSubtotal = orderItems.stream()
                            .filter(item -> {
                                Long prodId = variantToProductMap.get(item.getProductVariantId());
                                return prodId != null && targetProductIds.contains(prodId);
                            })
                            .mapToLong(item -> item.getUnitPrice() * item.getQuantity())
                            .sum();

                    if (targetSubtotal == 0) {
                        throw new BusinessException("Không có sản phẩm nào trong đơn hàng được áp dụng mã khuyến mãi này");
                    }

                    if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                        discountAmount = targetSubtotal * promotion.getDiscountValue() / 100L;
                        if (promotion.getMaxDiscountAmount() != null && discountAmount > promotion.getMaxDiscountAmount()) {
                            discountAmount = promotion.getMaxDiscountAmount();
                        }
                    } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                        discountAmount = Math.min(promotion.getDiscountValue(), targetSubtotal);
                    }
                } else {
                    // PromotionType.ORDER — áp dụng trên toàn bộ basePrice
                    if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                        discountAmount = basePriceLong * promotion.getDiscountValue() / 100L;
                        if (promotion.getMaxDiscountAmount() != null && discountAmount > promotion.getMaxDiscountAmount()) {
                            discountAmount = promotion.getMaxDiscountAmount();
                        }
                    } else if (promotion.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                        discountAmount = promotion.getDiscountValue();
                    }
                }

                paidPriceLong = Math.max(0, basePriceLong - discountAmount);
                appliedPromotion = promotion;
            }

            // 4. Khởi tạo Order từ mapper, sau đó gán các field business logic
            Order order = orderMapper.toEntity(request);
            order.setShopId(shopId);
            order.setBasePrice(basePriceLong);
            order.setPaidPrice(paidPriceLong);
            order.setDiscountAmount(discountAmount);
            order.setStatus(OrderStatus.PENDING);
            order.setProductQuantity(orderItems.stream().mapToInt(OrderItem::getQuantity).sum());

            for (OrderItem item : orderItems) {
                item.setOrder(order);
            }
            order.setItems(orderItems);

            // 5. Lưu đơn hàng vào Database
            Order result = orderRepository.save(order);

            // 6. Lưu thông tin sử dụng khuyến mãi (nếu áp dụng thành công)
            if (appliedPromotion != null) {
                PromotionUsage usage = PromotionUsage.builder()
                        .promotionId(appliedPromotion.getPromotionId())
                        .orderId(result.getOrderId())
                        .customerId(request.getCustomerId())
                        .shopId(shopId)
                        .discountAmount(discountAmount)
                        .status(PromotionUsageStatus.USED)
                        .createdAt(LocalDateTime.now())
                        .build();
                promotionUsageRepository.save(usage);

                if (appliedPromotion.getQuantity() != null) {
                    appliedPromotion.setQuantity(appliedPromotion.getQuantity() - 1);
                    promotionRepository.save(appliedPromotion);
                }
            }

            // 6.5. Gọi REST sang payment-service đồng bộ để tạo payment & payUrl (nếu lỗi sẽ rollback giao dịch)
            try {
                CreateOrderPaymentRequest paymentRequest = CreateOrderPaymentRequest.builder()
                        .orderId(result.getOrderId())
                        .orderCode(result.getCode())
                        .amount(result.getPaidPrice() != null ? result.getPaidPrice() : result.getBasePrice())
                        .paymentGateway(result.getPaymentGateway().name())
                        .build();

                Map<String, Object> paymentResponse = paymentServiceClient.createPaymentForOrder(paymentRequest);
                if (paymentResponse != null && paymentResponse.get("payUrl") != null) {
                    result.setPayUrl(paymentResponse.get("payUrl").toString());
                }
            } catch (Exception e) {
                log.error("Lỗi khi tạo link thanh toán đồng bộ qua payment-service cho orderId: {}", result.getOrderId(), e);
                throw new BusinessException("Không thể khởi tạo thanh toán: " + e.getMessage());
            }

            return result;
        });

        // 7. Publish event sang Kafka (fire & forget — không block chờ payUrl)
        try {
            orderEventProducer.publishOrderCreated(savedOrder);
        } catch (Exception e) {
            log.error("Lỗi khi publish event order.created lên Kafka cho orderId: {}", savedOrder.getOrderId(), e);
            throw new BusinessException("Lỗi hệ thống khi khởi động luồng xử lý đơn hàng");
        }

        // 8. Trả về response ngay lập tức — payUrl sẽ được lấy qua GET /orders/{id}/pay-url
        OrderResponse response = orderMapper.toResponse(savedOrder);
        enrichOrderItems(response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(BaseFilter filter, Long shopId) {
        return orderRepository.findAllByShopId(shopId, filter.toPageable())
                .map(this::mapWithPayUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Đơn hàng không tồn tại: " + id));
        return mapWithPayUrl(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Đơn hàng không tồn tại: " + id));
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Không thể thay đổi trạng thái đơn hàng đã hoàn tất hoặc đã hủy");
        }
        order.setStatus(status);
        return mapWithPayUrl(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCustomer(Long customerId, BaseFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("ShopId không tìm thấy trong context");
        }
        return orderRepository.findAllByShopIdAndCustomerId(shopId, customerId, filter.toPageable())
                .map(this::mapWithPayUrl);
    }

    private OrderResponse mapWithPayUrl(Order order) {
        OrderResponse response = orderMapper.toResponse(order);
        if (order.getCode() != null) {
            try {
                Map<String, Object> paymentData = paymentServiceClient.getPaymentByOrderCode(order.getCode());
                if (paymentData != null) {
                    String payUrl = toStringValue(paymentData.get("payUrl"));
                    // Chỉ set nếu là URL thật (tránh ghi đè bằng empty string)
                    if (payUrl != null && !payUrl.isBlank()) {
                        response.setPayUrl(payUrl);
                    }
                }
            } catch (feign.FeignException e) {
                if (e.status() == 400 || e.status() == 404) {
                    log.debug("PaymentOrder chưa tồn tại cho orderCode={}", order.getCode());
                } else {
                    log.warn("Lỗi HTTP {} khi lấy thông tin thanh toán cho orderCode={}: {}", e.status(), order.getCode(), e.getMessage());
                }
            } catch (Exception e) {
                log.warn("payment-service offline cho orderCode={}: {}", order.getCode(), e.getMessage());
            }
        }
        // Enrich productName & sizeName từ catalog-service theo productVariantId
        enrichOrderItems(response);
        return response;
    }


    private void enrichOrderItems(OrderResponse response) {
        if (response.getOrderItems() == null) return;
        response.getOrderItems().forEach(itemResponse -> {
            try {
                ApiResponse<Map<String, Object>> variantResp =
                        catalogServiceClient.getProductVariantById(itemResponse.getProductVariantId());
                if (variantResp != null && variantResp.getData() != null) {
                    Map<String, Object> variantData = variantResp.getData();
                    itemResponse.setSizeName(toStringValue(variantData.get("sizeName")));
                    Long productId = toLong(variantData.get("productId"));
                    if (productId != null) {
                        ApiResponse<Map<String, Object>> productResp =
                                catalogServiceClient.getProductById(productId);
                        if (productResp != null && productResp.getData() != null) {
                            itemResponse.setProductName(toStringValue(productResp.getData().get("name")));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Không thể enrich thông tin sản phẩm cho variantId={}: {}",
                        itemResponse.getProductVariantId(), e.getMessage());
            }
        });
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        return Long.parseLong(val.toString());
    }

    private String toStringValue(Object val) {
        return val == null ? "" : val.toString();
    }
}
