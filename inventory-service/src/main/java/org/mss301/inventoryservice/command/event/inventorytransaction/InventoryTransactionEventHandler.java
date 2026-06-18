package org.mss301.inventoryservice.command.event.inventorytransaction;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.mss301.inventoryservice.command.data.entity.IngredientBatch;
import org.mss301.inventoryservice.command.data.entity.InventoryTransaction;
import org.mss301.inventoryservice.command.data.entity.RawIngredient;
import org.mss301.inventoryservice.command.data.repository.InventoryTransactionRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryTransactionEventHandler {

    private final InventoryTransactionRepository repository;
    private final EntityManager entityManager;

    @EventHandler
    public void on(InventoryTransactionCreatedEvent event) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setId(event.getTransactionId());
        transaction.setShopId(event.getShopId());

        if (event.getRawIngredientId() != null) {
            transaction.setIngredient(entityManager.getReference(RawIngredient.class, event.getRawIngredientId()));
        }

        if (event.getBatchId() != null) {
            transaction.setBatch(entityManager.getReference(IngredientBatch.class, event.getBatchId()));
        }

        transaction.setTransactionType(event.getType());
        transaction.setQuantityAfter(event.getNewQuantity());
        transaction.setQuantityChange(event.getQuantity());

        repository.save(transaction);
    }
}
