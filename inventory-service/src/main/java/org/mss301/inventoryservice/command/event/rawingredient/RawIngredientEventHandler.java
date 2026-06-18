package org.mss301.inventoryservice.command.event.rawingredient;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.mss301.inventoryservice.command.data.entity.RawIngredient;
import org.mss301.inventoryservice.command.data.repository.RawIngredientRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RawIngredientEventHandler {

    private final RawIngredientRepository repository;

    @EventHandler
    public void on(RawIngredientCreatedEvent event) {
        RawIngredient entity = new RawIngredient();
        entity.setId(event.getId());
        entity.setShopId(event.getShopId());
        entity.setName(event.getName());
        entity.setBaseUnit(event.getBaseUnit());
        entity.setInventoryStatus(event.getStatus());
        repository.save(entity);
    }

    @EventHandler
    public void on(RawIngredientUpdatedEvent event) {
        RawIngredient entity = repository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Raw ingredient not found"));
        entity.setName(event.getName());
        entity.setInventoryStatus(event.getStatus());
        repository.save(entity);
    }

    @EventHandler
    public void on(RawIngredientDeletedEvent event) {
        repository.deleteById(event.getId());
    }
}