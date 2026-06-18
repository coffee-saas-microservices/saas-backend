package org.mss301.inventoryservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mss301.inventoryservice.dto.request.InvoiceItemRequest;
import org.mss301.inventoryservice.dto.response.InvoiceItemResponse;
import org.mss301.inventoryservice.entity.InventoryInvoiceDetail;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InvoiceItemMapper {

    @Mapping(source = "rawIngredient.id", target = "ingredientId")
    @Mapping(source = "rawIngredient.name", target = "ingredientName")
    @Mapping(source = "rawIngredient.baseUnit", target = "baseUnit")
    InvoiceItemResponse toResponse(InventoryInvoiceDetail entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inventoryInvoice", ignore = true)
    @Mapping(target = "rawIngredient", ignore = true)
    @Mapping(target = "convertedQuantity", ignore = true)
    @Mapping(target = "supplierName", ignore = true)
    InventoryInvoiceDetail toEntity(InvoiceItemRequest request);
}
