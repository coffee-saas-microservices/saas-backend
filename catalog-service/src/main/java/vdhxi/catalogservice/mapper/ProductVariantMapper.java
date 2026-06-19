package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.ProductVariantRequest;
import vdhxi.catalogservice.dto.response.ProductVariantResponse;
import vdhxi.catalogservice.entity.ProductVariant;

@Component
public class ProductVariantMapper {
    public ProductVariantResponse toResponse(ProductVariant entity) {
        if (entity == null) return null;
        ProductVariantResponse response = new ProductVariantResponse();
        response.setId(entity.getId());
        response.setShopId(entity.getShopId());
        if (entity.getProduct() != null) response.setProductId(entity.getProduct().getId());
        if (entity.getSize() != null) {
            response.setSizeId(entity.getSize().getId());
            response.setSizeName(entity.getSize().getName());
        }
        response.setPrice(entity.getPrice());
        response.setCode(entity.getCode());
        response.setStatus(entity.getStatus());
        return response;
    }

    public void updateEntity(ProductVariant entity, ProductVariantRequest request) {
        if (request.getPrice() != null) entity.setPrice(request.getPrice());
        if (request.getCode() != null) entity.setCode(request.getCode());
    }
}
