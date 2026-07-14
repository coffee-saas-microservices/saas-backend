package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.RecipeFilter;
import vdhxi.catalogservice.dto.request.RecipeRequest;
import vdhxi.catalogservice.dto.response.RecipeItemResponse;
import vdhxi.catalogservice.dto.response.RecipeResponse;
import java.util.List;

public interface RecipeService {
    RecipeResponse create(RecipeRequest request);
    Page<RecipeResponse> getByProductVariant(RecipeFilter filter);
    void delete(Long id);
    List<RecipeItemResponse> getRecipeItemsByVariantId(Long variantId);
    List<RecipeItemResponse> getRecipeItemsByToppingId(Long toppingId);
}