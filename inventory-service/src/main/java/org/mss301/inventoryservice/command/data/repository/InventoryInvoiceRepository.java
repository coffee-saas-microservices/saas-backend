package org.mss301.inventoryservice.command.data.repository;

import org.mss301.inventoryservice.command.data.entity.InventoryInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryInvoiceRepository extends JpaRepository<InventoryInvoice, String> {
}
