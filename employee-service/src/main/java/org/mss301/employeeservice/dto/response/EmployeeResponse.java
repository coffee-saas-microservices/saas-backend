package org.mss301.employeeservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.employeeservice.entity.enumeration.EmployeeStatus;
import org.mss301.employeeservice.entity.enumeration.EmployeeType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponse {

    private Long employeeId;
    private Long shopId;
    private Long userId;
    private EmployeeType employeeType;
    private Double hourlyWage;
    private Double weeklyHourLimit;
    private EmployeeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;
}
