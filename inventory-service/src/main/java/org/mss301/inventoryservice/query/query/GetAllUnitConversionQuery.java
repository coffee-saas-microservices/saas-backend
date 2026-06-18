package org.mss301.inventoryservice.query.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUnitConversionQuery {
    private UUID ingredientId;
    private InventoryStatus status;
}
