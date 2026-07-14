package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.dto.response.RecipeItemResponse;
import vdhxi.catalogservice.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/api/internal/recipes")
@RequiredArgsConstructor
public class InternalRecipeController {

    private final RecipeService recipeService;

    @GetMapping("/variant/{variantId}")
    public ApiResponse<List<RecipeItemResponse>> getRecipeByVariant(@PathVariable Long variantId) {
        List<RecipeItemResponse> items = recipeService.getRecipeItemsByVariantId(variantId);
        return ApiResponse.success(HttpStatus.OK, "Success", items, null);
    }

    @GetMapping("/topping/{toppingId}")
    public ApiResponse<List<RecipeItemResponse>> getRecipeByTopping(@PathVariable Long toppingId) {
        List<RecipeItemResponse> items = recipeService.getRecipeItemsByToppingId(toppingId);
        return ApiResponse.success(HttpStatus.OK, "Success", items, null);
    }
}
