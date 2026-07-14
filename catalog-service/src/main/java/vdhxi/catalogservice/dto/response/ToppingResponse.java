package vdhxi.catalogservice.dto.response;

import lombok.Data;
import vdhxi.catalogservice.enums.Status;

@Data
public class ToppingResponse {
    private Long id;
    private Long shopId;
    private String name;
    private Long price;
    private Status status;
}
