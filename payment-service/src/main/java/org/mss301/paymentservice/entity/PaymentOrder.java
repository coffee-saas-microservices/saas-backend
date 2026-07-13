package org.mss301.paymentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.paymentservice.entity.enumeration.PaymentStatus;
import org.mss301.paymentservice.entity.enumeration.ReferenceType;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_order")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long paymentOrderId;

    @Column(name = "order_code", unique = true, nullable = false)
    String orderCode;

    @Column(name = "amount", nullable = false)
    Long amount;

    @Column(name = "description")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    ReferenceType referenceType;

    @Column(name = "reference_id")
    String referenceId;
    @Column(name = "order_id")
    Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @Column(name = "pay_url", length = 1000)
    String payUrl;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
    }
}
