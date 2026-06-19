package vdhxi.catalogservice.dto.response;

import lombok.Data;
import vdhxi.catalogservice.enums.Status;

@Data
public class ProductVariantResponse {
    private Long id;
    private Long productId;
    private Long sizeId;
    private String sizeName;
    private Long shopId;
    private Float price;
    private String code;
    private Status status;
}
