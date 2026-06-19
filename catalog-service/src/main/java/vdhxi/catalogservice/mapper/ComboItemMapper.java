package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.ComboItemRequest;
import vdhxi.catalogservice.dto.response.ComboItemResponse;
import vdhxi.catalogservice.entity.ComboItem;

@Component
public class ComboItemMapper {
    public ComboItemResponse toResponse(ComboItem entity) {
        if (entity == null) return null;
        ComboItemResponse response = new ComboItemResponse();
        response.setId(entity.getId());
        if (entity.getProduct() != null) response.setProductId(entity.getProduct().getId());
        if (entity.getProductVariant() != null) response.setProductVariantId(entity.getProductVariant().getId());
        if (entity.getTopping() != null) response.setToppingId(entity.getTopping().getId());
        response.setName(entity.getName());
        response.setTotalPrice(entity.getTotalPrice());
        response.setUpSize(entity.getUpSize());
        response.setStatus(entity.getStatus());
        return response;
    }

    public void updateEntity(ComboItem entity, ComboItemRequest request) {
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getTotalPrice() != null) entity.setTotalPrice(request.getTotalPrice());
        if (request.getUpSize() != null) entity.setUpSize(request.getUpSize());
    }
}
