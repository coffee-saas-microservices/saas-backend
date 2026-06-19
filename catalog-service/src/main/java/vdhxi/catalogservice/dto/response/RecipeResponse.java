package vdhxi.catalogservice.dto.response;

import lombok.Data;
import vdhxi.catalogservice.enums.Status;

@Data
public class RecipeResponse {
    private Long id;
    private Long shopId;
    private Long rawIngredientId;
    private Long productVariantId;
    private Long toppingId;
    private Integer quantityRequired;
    private String note;
    private Status status;
}
