package vdhxi.catalogservice.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    private Long categoryId;
    private String name;
    private String imageUrl;
    private List<Long> allowedToppingIds;
}
