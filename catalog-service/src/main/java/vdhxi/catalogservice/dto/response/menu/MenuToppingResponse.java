package vdhxi.catalogservice.dto.response.menu;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuToppingResponse {
    private Long id;
    private String name;
    private Long price;
}
