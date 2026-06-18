package org.mss301.inventoryservice.command.data.repository;

import org.mss301.inventoryservice.command.data.entity.RawIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RawIngredientRepository extends JpaRepository<RawIngredient, UUID> {
}
