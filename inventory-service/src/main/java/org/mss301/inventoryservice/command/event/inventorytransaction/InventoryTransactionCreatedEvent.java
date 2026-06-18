package org.mss301.inventoryservice.command.event.inventorytransaction;

import lombok.Builder;
import lombok.Data;
import org.mss301.inventoryservice.command.data.enumeration.TransactionType;

import java.util.UUID;

@Data
@Builder
public class InventoryTransactionCreatedEvent {
    private String transactionId;
    private Long shopId;
    private String rawIngredientId;
    private String batchId; // Có thể null nếu không track theo lô
    private String referenceId; // ID của Invoice hoặc StockCheckSession
    private TransactionType type;
    private Double quantity; // Số lượng biến động
    private Double previousQuantity;
    private Double newQuantity;
    private String note;
}
