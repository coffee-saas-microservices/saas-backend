package vdhxi.catalogservice.dto.request;

import lombok.Data;

@Data
public class RecipeRequest {
    private Long rawIngredientId;
    private Long productVariantId;
    private Long toppingId;
    private Integer quantityRequired;
    private String note;
}
