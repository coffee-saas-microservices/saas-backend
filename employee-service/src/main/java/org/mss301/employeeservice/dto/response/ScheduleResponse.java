package org.mss301.employeeservice.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.employeeservice.entity.enumeration.ScheduleStatus;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleResponse {
    Long scheduleId;
    Long employeeId;
    String employeeName;
    String employeeType;
    DayOfWeek dayOfWeek;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String task;
    Boolean isRecurring;
    ScheduleStatus status;
}
