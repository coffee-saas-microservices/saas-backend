package org.mss301.employeeservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.employeeservice.entity.enumeration.ShiftTemplateStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftTemplateResponse {
    Long shiftTemplateId;
    Long shopId;
    String name;

    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime endTime;

    ShiftTemplateStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
