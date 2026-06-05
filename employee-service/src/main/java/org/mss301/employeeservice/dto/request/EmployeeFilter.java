package org.mss301.employeeservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.employeeservice.entity.enumeration.EmployeeStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeFilter extends BaseFilter {
    String keyword;
    EmployeeStatus status;
}
