package org.mss301.employeeservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftTemplateRequest {

    @NotBlank(message = "Tên ca làm việc không được để trống")
    String name;

    @NotBlank(message = "Giờ bắt đầu không được để trống")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$", message = "Giờ bắt đầu phải theo định dạng HH:mm:ss")
    String startTime;

    @NotBlank(message = "Giờ kết thúc không được để trống")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$", message = "Giờ kết thúc phải theo định dạng HH:mm:ss")
    String endTime;
}
