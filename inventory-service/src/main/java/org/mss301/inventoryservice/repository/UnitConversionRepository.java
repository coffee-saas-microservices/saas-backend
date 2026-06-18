package org.mss301.inventoryservice.repository;

import org.mss301.inventoryservice.entity.UnitConversion;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;
import org.mss301.inventoryservice.entity.enumeration.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitConversionRepository extends JpaRepository<UnitConversion, Long> {

    Optional<UnitConversion> findByIdAndShopId(Long id, Long shopId);

    boolean existsByIngredientIdAndFromUnitAndInventoryStatus(Long ingredientId, InputUnit fromUnit,
                                                              InventoryStatus inventoryStatus);

    Optional<UnitConversion> findByIngredientIdAndFromUnitAndInventoryStatus(Long ingredientId, InputUnit fromUnit,
                                                                             InventoryStatus inventoryStatus);
}
