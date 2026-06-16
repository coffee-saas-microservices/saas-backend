package org.mss301.inventoryservice.command.aggregate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.mss301.inventoryservice.command.command.CreateUnitConversionCommand;
import org.mss301.inventoryservice.command.command.DeleteUnitConversionCommand;
import org.mss301.inventoryservice.command.command.UpdateUnitConversionCommand;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InputUnit;
import org.mss301.inventoryservice.command.event.unitconversion.UnitConversionCreatedEvent;
import org.mss301.inventoryservice.command.event.unitconversion.UnitConversionDeletedEvent;
import org.mss301.inventoryservice.command.event.unitconversion.UnitConversionUpdatedEvent;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Aggregate
@NoArgsConstructor
@Getter
@Setter
public class UnitConversionAggregate {

    @AggregateIdentifier
    UUID id;
    UUID rawIngredientId;
    InputUnit fromUnit;
    BaseUnit toUnit;
    Double conversionFactor;
    Boolean isStandard;
    Long shopId;

    @CommandHandler
    public UnitConversionAggregate(CreateUnitConversionCommand command) {
        UnitConversionCreatedEvent event = new UnitConversionCreatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(UpdateUnitConversionCommand command) {
        UnitConversionUpdatedEvent event = new UnitConversionUpdatedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(DeleteUnitConversionCommand command) {
        UnitConversionDeletedEvent event = new UnitConversionDeletedEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(UnitConversionCreatedEvent event) {
        this.id = event.getId();
        this.rawIngredientId = event.getIngredientId();
        this.fromUnit = event.getFromUnit();
        this.toUnit = event.getToUnit();
        this.conversionFactor = event.getConversionFactor();
        this.isStandard = event.getIsStandard();
        this.shopId = event.getShopId();
    }

    @EventSourcingHandler
    public void on(UnitConversionUpdatedEvent event) {
        this.id = event.getId();
        this.rawIngredientId = event.getIngredientId();
        this.fromUnit = event.getFromUnit();
        this.toUnit = event.getToUnit();
        this.conversionFactor = event.getConversionFactor();
        this.isStandard = event.getIsStandard();
        this.shopId = event.getShopId();
    }

    @EventSourcingHandler
    public void on(UnitConversionDeletedEvent event) {
        this.id = event.getId();
    }
}
