package org.mss301.employeeservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.employeeservice.entity.enumeration.ShiftTemplateStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShiftTemplateFilter extends BaseFilter {
    String keyword;
    ShiftTemplateStatus status;
}
