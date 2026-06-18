package org.mss301.inventoryservice.command.command.rawingredient;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

@Data
@Builder
public class CreateRawIngredientCommand {
    @TargetAggregateIdentifier
    private String id;
    private Long shopId;
    private String name;
    private BaseUnit baseUnit;
    private InventoryStatus status;
}
