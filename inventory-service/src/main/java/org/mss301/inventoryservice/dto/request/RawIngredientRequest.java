package org.mss301.inventoryservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.BaseUnit;
import org.mss301.inventoryservice.entity.enumeration.StorageType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawIngredientRequest {

    @NotBlank(message = "Tên nguyên liệu không được để trống")
    String name;

    @NotBlank(message = "Mã SKU không được để trống")
    String skuCode;

    @NotNull(message = "Đơn vị gốc là bắt buộc")
    BaseUnit baseUnit;

    @Min(value = 0, message = "Cảnh báo tồn kho phải lớn hơn hoặc bằng 0")
    Double minStockAlert;

    @NotNull(message = "Loại bảo quản là bắt buộc")
    StorageType storageType;
}
