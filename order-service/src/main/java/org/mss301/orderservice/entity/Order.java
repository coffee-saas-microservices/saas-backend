package org.mss301.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.event.enumeration.OrderStepStatus;
import org.mss301.orderservice.entity.enumeration.OrderStatus;
import org.mss301.orderservice.entity.enumeration.OrderType;
import org.mss301.commonservice.dto.event.enumeration.PaymentGateway;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    //promotion

    @Column(name = "code", unique = true, nullable = false, length = 10)
    String code;

    @Column(name = "base_price")
    Long basePrice;

    @Column(name = "paid_price")
    Long paidPrice;

    @Column(name = "discount_amount")
    Long discountAmount;

    @Column(name = "product_quantity")
    Integer productQuantity;

    @Column(name = "invoice_url")
    String invoiceUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_gateway")
    PaymentGateway paymentGateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    OrderStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> items;

    @PrePersist
    public void prePersist() {
        if (status == null) status = OrderStatus.PENDING;
        if (code == null) code = "ORD-" + UUID.randomUUID().toString()
            .substring(0, 6).toUpperCase();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
