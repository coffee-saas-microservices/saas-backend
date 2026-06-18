package org.mss301.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.entity.enumeration.BaseUnit;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.entity.enumeration.StorageType;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_ingredients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "sku_code", nullable = false, length = 50)
    String skuCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "base_unit", nullable = false)
    BaseUnit baseUnit;

    @Column(name = "min_stock_alert")
    Double minStockAlert;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    StorageType storageType;

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
