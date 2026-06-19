package vdhxi.catalogservice.dto.response;

import lombok.Data;
import vdhxi.catalogservice.enums.Status;

@Data
public class ComboItemResponse {
    private Long id;
    private Long productId;
    private Long productVariantId;
    private Long toppingId;
    private String name;
    private Float totalPrice;
    private Boolean upSize;
    private Status status;
}
