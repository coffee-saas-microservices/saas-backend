package org.mss301.inventoryservice.repository;

import org.mss301.inventoryservice.entity.InventoryInvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryInvoiceDetailRepository extends JpaRepository<InventoryInvoiceDetail, Long> {
}
