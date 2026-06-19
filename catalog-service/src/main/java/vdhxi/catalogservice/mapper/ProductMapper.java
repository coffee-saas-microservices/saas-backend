package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.ProductRequest;
import vdhxi.catalogservice.dto.response.ProductResponse;
import vdhxi.catalogservice.entity.Product;
import java.util.ArrayList;

@Component
public class ProductMapper {
    public ProductResponse toResponse(Product entity) {
        if (entity == null) return null;
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setShopId(entity.getShopId());
        if (entity.getCategory() != null) {
            response.setCategoryId(entity.getCategory().getId());
            response.setCategoryName(entity.getCategory().getName());
        }
        response.setName(entity.getName());
        response.setImageUrl(entity.getImageUrl());
        response.setStatus(entity.getStatus());
        response.setAllowedToppings(new ArrayList<>()); // Handled by service
        return response;
    }

    public void updateEntity(Product entity, ProductRequest request) {
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getImageUrl() != null) entity.setImageUrl(request.getImageUrl());
    }
}
