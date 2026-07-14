package vdhxi.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vdhxi.catalogservice.enums.RecipeType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeItemResponse {
    private Long recipeId;
    private Long rawIngredientId;
    private Double quantityRequired;
    private RecipeType recipeType;
}
