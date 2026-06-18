package org.mss301.inventoryservice.command.command.rawingredient;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

@Data
@Builder
public class UpdateRawIngredientCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String description;
    private InventoryStatus status;
    private String imageUrl;
}
