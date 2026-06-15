package org.mss301.inventoryservice.command.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventory_invoices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryInvoice {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "created_by", nullable = false)
    Long createdBy;

    @Column(name = "code", nullable = false, length = 50)
    String code;

    @Column(name = "total_amount", nullable = false)
    Double totalAmount;

    @Column(name = "invoice_image_url", columnDefinition = "TEXT")
    String invoiceImageUrl;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InventoryStatus inventoryStatus;

    @OneToMany(mappedBy = "inventoryInvoice", cascade = CascadeType.ALL)
    private List<InventoryInvoiceDetail> details;
}
