package org.mss301.inventoryservice.command.event.inventoryinvoice;

import lombok.Builder;
import lombok.Data;
import org.mss301.inventoryservice.command.data.enumeration.TransactionType;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InventoryInvoiceCreatedEvent {
    private String invoiceId;
    private Long shopId;
    private TransactionType type;
    private String note;
    private String createdBy;
    private Double totalAmount;
    private List<InvoiceDetailEvent> details;

    @Data
    @Builder
    public static class InvoiceDetailEvent {
        private String ingredientId;
        private Double quantity;
        private Double price;
        private String note;
    }
}
