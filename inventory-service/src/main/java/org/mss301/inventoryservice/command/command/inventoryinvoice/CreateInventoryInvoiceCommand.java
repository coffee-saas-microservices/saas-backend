package org.mss301.inventoryservice.command.command.inventoryinvoice;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.mss301.inventoryservice.command.data.enumeration.TransactionType;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateInventoryInvoiceCommand {
    @TargetAggregateIdentifier
    private UUID invoiceId;
    private Long shopId;
    private TransactionType type;
    private String note;
    private String createdBy;
    private List<InvoiceDetailCommand> details;

    @Data
    @Builder
    public static class InvoiceDetailCommand {
        private String rawIngredientId;
        private Double quantity;
        private Double price;
        private String note;
    }
}
