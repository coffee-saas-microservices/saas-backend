package org.mss301.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToppingItemRequest {

    @NotNull(message = "ToppingId không được trống")
    Long toppingId;

    @NotNull(message = "Số lượng topping không được trống")
    @Min(value = 1, message = "Số lượng topping tối thiểu phải là 1")
    Integer quantity;
}
