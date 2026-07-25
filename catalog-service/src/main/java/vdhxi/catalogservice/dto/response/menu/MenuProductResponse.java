package vdhxi.catalogservice.dto.response.menu;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MenuProductResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private List<MenuVariantResponse> variants;
    private List<MenuToppingResponse> allowedToppings;
}
