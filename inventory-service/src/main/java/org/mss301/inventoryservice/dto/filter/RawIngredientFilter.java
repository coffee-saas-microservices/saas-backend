package org.mss301.inventoryservice.dto.filter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.mss301.inventoryservice.entity.enumeration.StorageType;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RawIngredientFilter extends BaseFilter {
    String keyword;
    StorageType storageType;
    Boolean isLowStock;
    InventoryStatus inventoryStatus;
}
