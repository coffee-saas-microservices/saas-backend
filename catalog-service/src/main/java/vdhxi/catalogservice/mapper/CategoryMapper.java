package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.CategoryRequest;
import vdhxi.catalogservice.dto.response.CategoryResponse;
import vdhxi.catalogservice.entity.Category;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;
        CategoryResponse response = new CategoryResponse();
        response.setId(entity.getId());
        response.setShopId(entity.getShopId());
        response.setName(entity.getName());
        response.setStatus(entity.getStatus());
        return response;
    }

    public void updateEntity(Category entity, CategoryRequest request) {
        if (request.getName() != null) entity.setName(request.getName());
    }
}
