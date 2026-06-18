package org.mss301.inventoryservice.command.event.ingredientbatch;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.mss301.inventoryservice.command.data.entity.IngredientBatch;
import org.mss301.inventoryservice.command.data.repository.IngredientBatchRepository;
import org.mss301.inventoryservice.command.data.repository.RawIngredientRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IngredientBatchEventHandler {

    private final IngredientBatchRepository repository;
    private final RawIngredientRepository rawIngredientRepository;

    @EventHandler
    public void on(IngredientBatchCreatedEvent event) {
        IngredientBatch batch = new IngredientBatch();
        batch.setId(event.getBatchId());
        batch.setShopId(event.getShopId());
        batch.setRawIngredient(rawIngredientRepository.getReferenceById(event.getRawIngredientId()));
        batch.setInitialQuantity(event.getInitialQuantity());
        //batch.setRemainingQuantity(event.getInitialQuantity());
        batch.setExpiredAt(event.getExpirationDate());
        batch.setImportPrice(event.getUnitPrice());
        batch.setSupplierName(event.getVendor());
        repository.save(batch);
    }

    @EventHandler
    public void on(BatchQuantityUpdatedEvent event) {
        IngredientBatch batch = repository.findById(event.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        //batch.setRemainingQuantity(event.getNewRemainingQuantity());
        repository.save(batch);
    }
}
