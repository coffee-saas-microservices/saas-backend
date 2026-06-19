package vdhxi.catalogservice.dto.request;

import lombok.Data;

@Data
public class ProductVariantRequest {
    private Long productId;
    private Long sizeId;
    private Float price;
    private String code;
}
