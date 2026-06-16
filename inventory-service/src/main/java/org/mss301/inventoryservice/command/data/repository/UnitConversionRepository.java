package org.mss301.inventoryservice.command.data.repository;

import org.mss301.inventoryservice.command.data.entity.UnitConversion;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnitConversionRepository extends JpaRepository<UnitConversion, UUID> {
    List<UnitConversion> findAllByIngredientIdAndInventoryStatus(UUID ingredientId, InventoryStatus inventoryStatus);
}
