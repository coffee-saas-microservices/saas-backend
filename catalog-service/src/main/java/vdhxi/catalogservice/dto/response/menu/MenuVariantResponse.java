package vdhxi.catalogservice.dto.response.menu;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuVariantResponse {
    private Long id;
    private String sizeName;
    private Float price;
    private String code;
}
