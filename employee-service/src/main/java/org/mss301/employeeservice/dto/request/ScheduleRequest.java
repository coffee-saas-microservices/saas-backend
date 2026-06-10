package org.mss301.employeeservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRequest {
    Long employeeId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String task;
    Boolean isRecurring;
}
