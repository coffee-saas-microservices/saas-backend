package org.mss301.inventoryservice.command.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.command.data.enumeration.InputUnit;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_invoice_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryInvoiceDetail {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    InventoryInvoice inventoryInvoice;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    RawIngredient rawIngredient;

    @Column(name = "supplier_name")
    String supplierName;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_unit", nullable = false)
    InputUnit inputUnit;

    @Column(name = "input_quantity", nullable = false)
    Double inputQuantity;

    @Column(name = "converted_quantity", nullable = false)
    Double convertedQuantity;

    @Column(name = "unit_price", nullable = false)
    Double unitPrice;

    @Column(name = "batch_code", length = 50)
    String batchCode;

    @Column(name = "expired_at", nullable = false)
    LocalDateTime expiredAt;

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
