package org.mss301.inventoryservice.command.event.ingredientbatch;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class IngredientBatchCreatedEvent {
    private String batchId;
    private Long shopId;
    private String rawIngredientId;
    private Double initialQuantity;
    private LocalDateTime expirationDate;
    private Double unitPrice;
    private String vendor;
}
