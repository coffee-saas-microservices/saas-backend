package org.mss301.inventoryservice.command.command.rawingredient;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class DeleteRawIngredientCommand {
    @TargetAggregateIdentifier
    private String id;
}
