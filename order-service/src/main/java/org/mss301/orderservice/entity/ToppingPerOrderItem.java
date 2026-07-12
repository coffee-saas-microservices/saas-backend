package org.mss301.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.ToppingPerOrderItemStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "topping_per_order_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToppingPerOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long toppingPerOrderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    OrderItem orderItem;

    @Column(name = "topping_id", nullable = false)
    Long toppingId;

    @Column(name = "topping_name")
    String toppingName;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "unit_price")
    Long unitPrice;

    @Column(name = "topping_per_order_item_status")
    @Enumerated(EnumType.STRING)
    ToppingPerOrderItemStatus status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
