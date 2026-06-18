package org.mss301.inventoryservice.command.command.inventorytransaction;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.mss301.inventoryservice.command.data.enumeration.TransactionType;

@Data
@Builder
public class CreateInventoryTransactionCommand {
    @TargetAggregateIdentifier
    private String transactionId;
    private String shopId;
    private String rawIngredientId;
    private String batchId; // Có thể null nếu không track theo lô
    private String referenceId; // ID của Invoice hoặc StockCheckSession
    private TransactionType type;
    private Double quantity; // Số lượng biến động
    private Double previousQuantity;
    private Double newQuantity;
    private String note;
}
