package org.mss301.inventoryservice.command.event.rawingredient;

import lombok.Builder;
import lombok.Data;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

@Data
@Builder
public class RawIngredientUpdatedEvent {
    private String id;
    private String name;
    private String description;
    private InventoryStatus status;
    private String imageUrl;
}
