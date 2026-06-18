package org.mss301.inventoryservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.mss301.inventoryservice.command.command.ingredientbatch.CreateIngredientBatchCommand;
import org.mss301.inventoryservice.command.command.ingredientbatch.UpdateBatchQuantityCommand;
import org.mss301.inventoryservice.command.event.ingredientbatch.BatchQuantityUpdatedEvent;
import org.mss301.inventoryservice.command.event.ingredientbatch.IngredientBatchCreatedEvent;

@Aggregate
public class IngredientBatchAggregate {

    @AggregateIdentifier
    private String batchId;
    private Double remainingQuantity;

    public IngredientBatchAggregate() {}

    @CommandHandler
    public IngredientBatchAggregate(CreateIngredientBatchCommand command) {
        if (command.getInitialQuantity() <= 0) {
            throw new IllegalArgumentException("Initial quantity must be greater than zero");
        }
        AggregateLifecycle.apply(IngredientBatchCreatedEvent.builder()
                .batchId(command.getBatchId())
                .shopId(command.getShopId())
                .rawIngredientId(command.getRawIngredientId())
                .initialQuantity(command.getInitialQuantity())
                .expirationDate(command.getExpirationDate())
                .unitPrice(command.getUnitPrice())
                .vendor(command.getVendor())
                .build());
    }

    @EventSourcingHandler
    public void on(IngredientBatchCreatedEvent event) {
        this.batchId = event.getBatchId();
        this.remainingQuantity = event.getInitialQuantity();
    }

    @CommandHandler
    public void handle(UpdateBatchQuantityCommand command) {
        Double newQuantity = this.remainingQuantity + command.getQuantityChange();
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Not enough quantity in this batch");
        }
        AggregateLifecycle.apply(BatchQuantityUpdatedEvent.builder()
                .batchId(command.getBatchId())
                .quantityChange(command.getQuantityChange())
                .newRemainingQuantity(newQuantity)
                .reason(command.getReason())
                .build());
    }

    @EventSourcingHandler
    public void on(BatchQuantityUpdatedEvent event) {
        this.remainingQuantity = event.getNewRemainingQuantity();
    }
}
