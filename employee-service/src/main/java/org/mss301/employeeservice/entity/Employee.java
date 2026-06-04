package org.mss301.employeeservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.employeeservice.entity.enumeration.EmployeeStatus;
import org.mss301.employeeservice.entity.enumeration.EmployeeType;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long employeeId;

    @Column(name = "shop_id", nullable = false)
    Long shopId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    EmployeeType employeeType;

    @Column(name = "hourly_wage")
    Double hourlyWage;

    @Column(name = "weekly_hour_limit")
    Double weeklyHourLimit;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    EmployeeStatus status;

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = EmployeeStatus.ACTIVE;
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
