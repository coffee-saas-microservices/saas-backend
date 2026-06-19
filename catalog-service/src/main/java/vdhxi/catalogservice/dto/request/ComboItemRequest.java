package vdhxi.catalogservice.dto.request;

import lombok.Data;

@Data
public class ComboItemRequest {
    private Long productId;
    private Long productVariantId;
    private Long toppingId;
    private String name;
    private Float totalPrice;
    private Boolean upSize;
}
