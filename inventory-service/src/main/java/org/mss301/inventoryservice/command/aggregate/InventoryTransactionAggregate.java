package org.mss301.inventoryservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.mss301.inventoryservice.command.command.inventorytransaction.CreateInventoryTransactionCommand;
import org.mss301.inventoryservice.command.event.inventorytransaction.InventoryTransactionCreatedEvent;

@Aggregate
public class InventoryTransactionAggregate {

    @AggregateIdentifier
    private String transactionId;

    public InventoryTransactionAggregate() {}

    @CommandHandler
    public InventoryTransactionAggregate(CreateInventoryTransactionCommand command) {
        // Validation cơ bản
        AggregateLifecycle.apply(InventoryTransactionCreatedEvent.builder()
                .transactionId(command.getTransactionId())
                .shopId(command.getShopId())
                .rawIngredientId(command.getRawIngredientId())
                .batchId(command.getBatchId())
                .referenceId(command.getReferenceId())
                .type(command.getType())
                .quantity(command.getQuantity())
                .previousQuantity(command.getPreviousQuantity())
                .newQuantity(command.getNewQuantity())
                .note(command.getNote())
                .build());
    }

    @EventSourcingHandler
    public void on(InventoryTransactionCreatedEvent event) {
        this.transactionId = event.getTransactionId();
    }
}
