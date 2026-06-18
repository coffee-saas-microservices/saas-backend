package org.mss301.inventoryservice.service.inter;

import org.mss301.inventoryservice.dto.filter.InventoryInvoiceFilter;
import org.mss301.inventoryservice.dto.request.InventoryInvoiceRequest;
import org.mss301.inventoryservice.dto.response.InventoryInvoiceResponse;
import org.springframework.data.domain.Page;

public interface InventoryInvoiceService {
    InventoryInvoiceResponse importStock(InventoryInvoiceRequest request);
    Page<InventoryInvoiceResponse> getAll(InventoryInvoiceFilter filter);
    InventoryInvoiceResponse getDetail(Long id);
    //void deductStock(Long shopId, Long variantId, Long toppingId, Double quantity, Long orderId);
}
