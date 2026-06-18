package org.mss301.inventoryservice.command.data.repository;

import org.mss301.inventoryservice.command.data.entity.IngredientBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngredientBatchRepository extends JpaRepository<IngredientBatch, String> {
}
