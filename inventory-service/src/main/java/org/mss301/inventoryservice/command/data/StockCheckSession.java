package org.mss301.inventoryservice.command.data;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_check_sessions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCheckSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "code", length = 50)
    String code;

    @Column(name = "created_by", nullable = false)
    Long createdBy;

    @Column(name = "approved_by")
    Long approvedBy;

    @Column(name = "is_approved", nullable = false)
    Boolean isApproved;

    @Column(name = "note", columnDefinition = "TEXT")
    String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InventoryStatus inventoryStatus;
}
