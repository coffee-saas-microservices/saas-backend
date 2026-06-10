package org.mss301.employeeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.employeeservice.entity.enumeration.UnavailabilityStatus;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_unavailabilities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeUnavailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long employeeUnavailabilityId;

    @Column(name = "shop_id")
    Long shopId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Column(name = "day_of_week")
    DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    LocalDateTime startTime;

    @Column(name = "end_time")
    LocalDateTime endTime;

    @Column(name = "specific_date")
    LocalDateTime specificDate;

    @Column(name = "reason")
    String reason;

    @Column(name = "is_recurring")
    Boolean isRecurring;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Column(name = "status") // nullable = false
    UnavailabilityStatus status;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = UnavailabilityStatus.ACTIVE;
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
