package org.mss301.inventoryservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceItemResponse {
    Long ingredientId;
    String ingredientName;
    InputUnit inputUnit;
    Integer inputQuantity;
    Double unitPrice;
    Integer convertedQuantity;
    String baseUnit;
    String batchCode;
    LocalDate expiredAt;
}
