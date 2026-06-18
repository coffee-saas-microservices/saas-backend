package org.mss301.inventoryservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mss301.inventoryservice.dto.request.InventoryInvoiceRequest;
import org.mss301.inventoryservice.dto.response.InventoryInvoiceResponse;
import org.mss301.inventoryservice.entity.InventoryInvoice;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        InvoiceItemMapper.class })
public interface InventoryInvoiceMapper {

    @Mapping(target = "items", source = "details")
    @Mapping(target = "importedAt", source = "createdAt")
    @Mapping(target = "supplierName", expression = "java(entity.getDetails() != null && !entity.getDetails().isEmpty() ? entity.getDetails().get(0).getSupplierName() : null)")
    @Mapping(target = "createdByName", ignore = true)
    InventoryInvoiceResponse toResponse(InventoryInvoice entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "inventoryStatus", ignore = true)
    InventoryInvoice toEntity(InventoryInvoiceRequest request);
}
