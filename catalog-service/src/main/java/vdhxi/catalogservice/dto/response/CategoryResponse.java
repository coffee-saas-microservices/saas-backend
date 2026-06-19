package vdhxi.catalogservice.dto.response;

import lombok.Data;
import vdhxi.catalogservice.enums.Status;

@Data
public class CategoryResponse {
    private Long id;
    private Long shopId;
    private String name;
    private String code;
    private Status status;
}
