package org.mss301.inventoryservice.command.data;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingredient_batches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    RawIngredient rawIngredient;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "batch_code", length = 50)
    String batchCode;

    @Column(name = "supplier_name")
    String supplierName;

    @Column(name = "expired_at", nullable = false)
    LocalDateTime expiredAt;

    @Column(name = "initial_quantity", nullable = false)
    Double initialQuantity;

    @Column(name = "current_quantity", nullable = false)
    Double currentQuantity;

    @Column(name = "import_price", nullable = false)
    Double importPrice;

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
