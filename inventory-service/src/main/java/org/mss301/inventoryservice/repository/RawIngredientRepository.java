package org.mss301.inventoryservice.repository;

import org.mss301.inventoryservice.entity.RawIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RawIngredientRepository
        extends JpaRepository<RawIngredient, Long>, JpaSpecificationExecutor<RawIngredient> {
    Optional<RawIngredient> findByIdAndShopId(Long id, Long shopId);

    List<RawIngredient> findAllByShopId(Long shopId);
}
