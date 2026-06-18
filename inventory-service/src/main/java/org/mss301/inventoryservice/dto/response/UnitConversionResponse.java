package org.mss301.inventoryservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.BaseUnit;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitConversionResponse {

    Long id;
    Long ingredientId;
    String ingredientName;
    InputUnit fromUnit;
    BaseUnit toUnit;
    Double conversionFactor;
    Boolean isStandard;
    InventoryStatus inventoryStatus;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
