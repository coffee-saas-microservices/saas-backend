package org.mss301.employeeservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.mss301.employeeservice.entity.enumeration.EmployeeType;

@Data
public class UpdateEmployeeRequest {

    @NotNull(message = "employeeType không được để trống")
    private EmployeeType employeeType;

    private Double hourlyWage;

    private Double weeklyHourLimit;
}
