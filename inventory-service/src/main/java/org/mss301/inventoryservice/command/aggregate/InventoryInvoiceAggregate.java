package org.mss301.inventoryservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.mss301.inventoryservice.command.command.inventoryinvoice.CreateInventoryInvoiceCommand;
import org.mss301.inventoryservice.command.event.inventoryinvoice.InventoryInvoiceCreatedEvent;

import java.util.stream.Collectors;

@Aggregate
public class InventoryInvoiceAggregate {

    @AggregateIdentifier
    private String invoiceId;

    public InventoryInvoiceAggregate() {
        // Axon requires empty constructor
    }

    @CommandHandler
    public InventoryInvoiceAggregate(CreateInventoryInvoiceCommand command) {
        // Business Validation ở đây (ví dụ check số lượng > 0)

        Double total = command.getDetails().stream()
                .mapToDouble(d -> d.getQuantity() * d.getPrice())
                .sum();

        InventoryInvoiceCreatedEvent event = InventoryInvoiceCreatedEvent.builder()
                .invoiceId(command.getInvoiceId())
                .shopId(command.getShopId())
                .type(command.getType())
                .note(command.getNote())
                .createdBy(command.getCreatedBy())
                .totalAmount(total)
                .details(command.getDetails().stream()
                        .map(d -> InventoryInvoiceCreatedEvent.InvoiceDetailEvent.builder()
                                .rawIngredientId(d.getRawIngredientId())
                                .quantity(d.getQuantity())
                                .price(d.getPrice())
                                .note(d.getNote())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(InventoryInvoiceCreatedEvent event) {
        this.invoiceId = event.getInvoiceId();
    }
}
