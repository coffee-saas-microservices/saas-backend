package org.mss301.inventoryservice.command.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InputUnit;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitConversionRequest {

    @NotNull(message = "Nguyên liệu ID là bắt buộc")
    UUID ingredientId;

    @NotNull(message = "Đơn vị nhập là bắt buộc")
    InputUnit fromUnit;

    @NotNull(message = "Đơn vị output là bắt buộc")
    BaseUnit toUnit;

    @NotNull(message = "Hệ số quy đổi là bắt buộc")
    @Min(value = 1, message = "Hệ số quy đổi phải lớn hơn 0")
    Double conversionFactor;

    Boolean isStandard;
}
