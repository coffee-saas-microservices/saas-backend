package org.mss301.inventoryservice.command.data.repository;

import org.mss301.inventoryservice.command.data.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, String> {
}
