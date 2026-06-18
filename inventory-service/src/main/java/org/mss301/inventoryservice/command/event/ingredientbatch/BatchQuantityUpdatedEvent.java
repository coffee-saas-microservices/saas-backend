package org.mss301.inventoryservice.command.event.ingredientbatch;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BatchQuantityUpdatedEvent {
    private String batchId;
    private Double quantityChange;
    private Double newRemainingQuantity;
    private String reason;
}
