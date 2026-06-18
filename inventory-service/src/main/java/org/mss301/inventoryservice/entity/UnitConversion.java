package org.mss301.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.entity.enumeration.BaseUnit;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "unit_conversions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitConversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_unit", nullable = false)
    InputUnit fromUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_unit", nullable = false)
    BaseUnit toUnit;

    @Column(name = "conversion_factor", nullable = false)
    Double conversionFactor;

    @Column(name = "is_standard", nullable = false)
    Boolean isStandard;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    RawIngredient ingredient;

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
