package org.mss301.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.PointHistoryStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_histories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long pointHistoryId;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @Column(name = "order_id", nullable = false)
    Long orderId;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "point_change")
    Integer pointChange;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    PointHistoryStatus status;
}
