package org.mss301.inventoryservice.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.mss301.inventoryservice.command.command.rawingredient.CreateRawIngredientCommand;
import org.mss301.inventoryservice.command.command.rawingredient.DeleteRawIngredientCommand;
import org.mss301.inventoryservice.command.command.rawingredient.UpdateRawIngredientCommand;
import org.mss301.inventoryservice.command.event.rawingredient.RawIngredientCreatedEvent;
import org.mss301.inventoryservice.command.event.rawingredient.RawIngredientDeletedEvent;
import org.mss301.inventoryservice.command.event.rawingredient.RawIngredientUpdatedEvent;

@Aggregate
public class RawIngredientAggregate {

    @AggregateIdentifier
    private String id;

    public RawIngredientAggregate() {
    }

    @CommandHandler
    public RawIngredientAggregate(CreateRawIngredientCommand command) {
        AggregateLifecycle.apply(RawIngredientCreatedEvent.builder()
                .id(command.getId())
                .shopId(command.getShopId())
                .name(command.getName())
                .description(command.getDescription())
                .baseUnit(command.getBaseUnit())
                .status(command.getStatus())
                .imageUrl(command.getImageUrl())
                .build());
    }

    @EventSourcingHandler
    public void on(RawIngredientCreatedEvent event) {
        this.id = event.getId();
    }

    @CommandHandler
    public void handle(UpdateRawIngredientCommand command) {
        AggregateLifecycle.apply(RawIngredientUpdatedEvent.builder()
                .id(command.getId())
                .name(command.getName())
                .description(command.getDescription())
                .status(command.getStatus())
                .imageUrl(command.getImageUrl())
                .build());
    }

    @EventSourcingHandler
    public void on(RawIngredientUpdatedEvent event) {
    }

    @CommandHandler
    public void handle(DeleteRawIngredientCommand command) {
        AggregateLifecycle.apply(RawIngredientDeletedEvent.builder()
                .id(command.getId())
                .build());
    }

    @EventSourcingHandler
    public void on(RawIngredientDeletedEvent event) {
    }
}