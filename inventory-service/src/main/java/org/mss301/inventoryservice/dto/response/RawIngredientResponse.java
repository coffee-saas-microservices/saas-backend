package org.mss301.inventoryservice.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mss301.inventoryservice.entity.enumeration.BaseUnit;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.entity.enumeration.StorageType;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawIngredientResponse {
    Long id;
    String name;
    String skuCode;
    BaseUnit baseUnit;
    Integer minStockAlert;
    StorageType storageType;
    Double totalStockQuantity;
    InventoryStatus inventoryStatus;
}
