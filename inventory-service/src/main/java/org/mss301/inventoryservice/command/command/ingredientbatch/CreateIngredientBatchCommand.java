package org.mss301.inventoryservice.command.command.ingredientbatch;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateIngredientBatchCommand {
    @TargetAggregateIdentifier
    private String batchId;
    private String shopId;
    private String rawIngredientId;
    private Double initialQuantity;
    private LocalDateTime expirationDate;
    private Double unitPrice;
    private String vendor;
}
