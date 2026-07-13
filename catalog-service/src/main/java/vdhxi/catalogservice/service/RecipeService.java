package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.request.RecipeRequest;
import vdhxi.catalogservice.dto.response.RecipeItemResponse;
import vdhxi.catalogservice.dto.response.RecipeResponse;
import java.util.List;

public interface RecipeService {
    RecipeResponse create(RecipeRequest request);
    List<RecipeResponse> getByProductVariant(Long variantId);
    void delete(Long id);

    List<RecipeItemResponse> getRecipeItemsByVariantId(Long variantId);
    List<RecipeItemResponse> getRecipeItemsByToppingId(Long toppingId);
}
