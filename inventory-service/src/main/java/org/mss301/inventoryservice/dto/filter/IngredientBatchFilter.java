package org.mss301.inventoryservice.dto.filter;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.mss301.commonservice.dto.request.BaseFilter;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientBatchFilter extends BaseFilter {
    Long ingredientId;
    String batchCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate expiredBeforeDate;
    Boolean hasRemainingQuantity;
    InventoryStatus inventoryStatus;
}
