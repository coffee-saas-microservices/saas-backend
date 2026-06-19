package vdhxi.catalogservice.mapper;

import org.springframework.stereotype.Component;
import vdhxi.catalogservice.dto.request.RecipeRequest;
import vdhxi.catalogservice.dto.response.RecipeResponse;
import vdhxi.catalogservice.entity.Recipe;

@Component
public class RecipeMapper {
    public RecipeResponse toResponse(Recipe entity) {
        if (entity == null) return null;
        RecipeResponse response = new RecipeResponse();
        response.setId(entity.getId());
        response.setShopId(entity.getShopId());
        response.setRawIngredientId(entity.getRawIngredientId());
        if (entity.getProductVariant() != null) response.setProductVariantId(entity.getProductVariant().getId());
        if (entity.getTopping() != null) response.setToppingId(entity.getTopping().getId());
        response.setQuantityRequired(entity.getQuantityRequired());
        response.setNote(entity.getNote());
        response.setStatus(entity.getStatus());
        return response;
    }
}
