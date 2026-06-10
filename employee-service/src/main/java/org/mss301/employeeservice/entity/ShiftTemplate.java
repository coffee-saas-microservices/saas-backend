package org.mss301.employeeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.employeeservice.entity.enumeration.ShiftTemplateStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shift_templates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long shiftTemplateId;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "name")
    String name;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ShiftTemplateStatus status;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = ShiftTemplateStatus.ACTIVE;
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
