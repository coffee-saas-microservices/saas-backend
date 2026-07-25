package vdhxi.catalogservice.dto.response.menu;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MenuCategoryResponse {
    private Long id;
    private String name;
    private List<MenuProductResponse> products;
}
