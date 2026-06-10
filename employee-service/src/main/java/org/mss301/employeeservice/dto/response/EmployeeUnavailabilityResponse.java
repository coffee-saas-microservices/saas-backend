package org.mss301.employeeservice.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.employeeservice.entity.enumeration.UnavailabilityStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeUnavailabilityResponse {
    Long employeeUnavailabilityId;
    Long employeeId;
    String employeeName;
    String reason;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime specificDate;
    Boolean isRecurring;
    UnavailabilityStatus status;
}
