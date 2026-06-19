package vdhxi.catalogservice.dto.response;

import lombok.Data;
import vdhxi.catalogservice.enums.Status;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private Long shopId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String imageUrl;
    private Status status;
    private List<ToppingResponse> allowedToppings;
}
