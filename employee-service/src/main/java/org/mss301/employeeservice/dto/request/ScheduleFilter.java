package org.mss301.employeeservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.employeeservice.entity.enumeration.ScheduleStatus;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleFilter extends BaseFilter {
    Long employeeId;
    DayOfWeek dayOfWeek;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Boolean isRecurring;
    ScheduleStatus status;
}
