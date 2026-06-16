package org.mss301.inventoryservice.query.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InputUnit;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitConversionResponse {

    UUID id;
    UUID ingredientId;
    Long shopId;
    String ingredientName;
    InputUnit fromUnit;
    BaseUnit toUnit;
    Double conversionFactor;
    Boolean isStandard;
    InventoryStatus inventoryStatus;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
