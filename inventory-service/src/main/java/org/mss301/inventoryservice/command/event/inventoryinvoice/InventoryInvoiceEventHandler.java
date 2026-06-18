package org.mss301.inventoryservice.command.event.inventoryinvoice;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.mss301.inventoryservice.command.data.entity.InventoryInvoice;
import org.mss301.inventoryservice.command.data.entity.InventoryInvoiceDetail;
import org.mss301.inventoryservice.command.data.repository.InventoryInvoiceRepository;
import org.mss301.inventoryservice.command.data.repository.RawIngredientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryInvoiceEventHandler {

    private final InventoryInvoiceRepository repository;
    private final RawIngredientRepository rawIngredientRepository;

    @EventHandler
    public void on(InventoryInvoiceCreatedEvent event) {
        InventoryInvoice invoice = new InventoryInvoice();
        invoice.setId(event.getInvoiceId());
        invoice.setShopId(event.getShopId());
        invoice.setTotalAmount(event.getTotalAmount());
        invoice.setNote(event.getNote());
        // set audit fields...

        List<InventoryInvoiceDetail> details = event.getDetails().stream().map(d -> {
            InventoryInvoiceDetail detail = new InventoryInvoiceDetail();
            detail.setInventoryInvoice(invoice);
            detail.setRawIngredient(rawIngredientRepository.findById(d.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Raw Ingredient not found")));
            detail.setInputQuantity(d.getQuantity());
            detail.setUnitPrice(d.getPrice());
            return detail;
        }).collect(Collectors.toList());

        invoice.setDetails(details);
        repository.save(invoice);

        // CHÚ Ý RẤT QUAN TRỌNG:
        // Trong CQRS xịn, việc cộng/trừ số lượng tồn kho (InventoryTransaction/IngredientBatch)
        // nên được trigger bằng cách emit thêm Event (ví dụ StockAddedEvent) hoặc dùng Saga
        // để gọi tiếp các Command của IngredientBatchAggregate.
    }
}
