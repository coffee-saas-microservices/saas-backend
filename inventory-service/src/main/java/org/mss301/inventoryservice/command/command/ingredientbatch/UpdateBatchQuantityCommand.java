package org.mss301.inventoryservice.command.command.ingredientbatch;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class UpdateBatchQuantityCommand {
    @TargetAggregateIdentifier
    private String batchId;
    private Double quantityChange;
    private String reason;
}
