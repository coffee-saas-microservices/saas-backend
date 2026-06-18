package org.mss301.inventoryservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceItemRequest {
    @NotNull(message = "Nguyên liệu là bắt buộc")
    Long ingredientId;
    @NotNull(message = "Đơn vị nhập là bắt buộc")
    InputUnit inputUnit;
    @NotNull(message = "Số lượng nhập là bắt buộc")
    @Min(value = 1)
    Double inputQuantity;
    @NotNull(message = "Đơn giá nhập là bắt buộc")
    @Min(value = 0)
    Double unitPrice;
    String batchCode;
    @NotNull(message = "Hạn sử dụng là bắt buộc để quản lý FIFO")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate expiredAt;
}
