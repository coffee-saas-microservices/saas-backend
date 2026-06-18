package org.mss301.inventoryservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.BaseUnit;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitConversionRequest {

    @NotNull(message = "Nguyên liệu ID là bắt buộc")
    Long ingredientId;

    @NotNull(message = "Đơn vị nhập là bắt buộc")
    InputUnit fromUnit;

    @NotNull(message = "Đơn vị output là bắt buộc")
    BaseUnit toUnit;

    @NotNull(message = "Hệ số quy đổi là bắt buộc")
    @Min(value = 1, message = "Hệ số quy đổi phải lớn hơn 0")
    Double conversionFactor;

    Boolean isStandard;
}
