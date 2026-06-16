package org.mss301.inventoryservice.command.event.unitconversion;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.mss301.inventoryservice.command.data.entity.RawIngredient;
import org.mss301.inventoryservice.command.data.entity.UnitConversion;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;
import org.mss301.inventoryservice.command.data.repository.RawIngredientRepository;
import org.mss301.inventoryservice.command.data.repository.UnitConversionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnitConversionEventHandler {

    private final UnitConversionRepository unitConversionRepository;
    private final RawIngredientRepository rawIngredientRepository;

    @EventHandler
    public void on(UnitConversionCreatedEvent event) {

        UnitConversion entity = new UnitConversion();
        BeanUtils.copyProperties(event, entity);

        RawIngredient ingredientRef = rawIngredientRepository.getReferenceById(event.getIngredientId());
        entity.setIngredient(ingredientRef);

        entity.setInventoryStatus(InventoryStatus.ACTIVE);

        unitConversionRepository.save(entity);
    }

    @EventHandler
    public void on(UnitConversionUpdatedEvent event) {

        Optional<UnitConversion> entity = unitConversionRepository.findById(event.getId());

        entity.ifPresent(unitConversion -> {
            BeanUtils.copyProperties(event, unitConversion);
            unitConversionRepository.save(unitConversion);
        });
    }

    @EventHandler
    public void on(UnitConversionDeletedEvent event) {

        Optional<UnitConversion> entity = unitConversionRepository.findById(event.getId());

        entity.ifPresent(unitConversion -> {
            unitConversion.setInventoryStatus(InventoryStatus.DELETED);
            unitConversionRepository.save(unitConversion);
        });
    }
}
