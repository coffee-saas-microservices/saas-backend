package org.mss301.inventoryservice.command.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_check_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCheckDetail {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    StockCheckSession session;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    RawIngredient ingredient;

    @Column(name = "snapshot_quantity", nullable = false)
    Double snapshotQuantity;

    @Column(name = "actual_quantity")
    Double actualQuantity;

    @Column(name = "diff_quantity")
    Double diffQuantity;

    @Column(name = "reason", columnDefinition = "TEXT")
    String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InventoryStatus inventoryStatus;
}
