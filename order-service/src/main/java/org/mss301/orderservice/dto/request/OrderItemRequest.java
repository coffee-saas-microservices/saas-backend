package org.mss301.orderservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {

    @NotNull(message = "ProductVariantId không được trống")
    Long productVariantId;

    @NotNull(message = "Số lượng sản phẩm không được trống")
    @Min(value = 1, message = "Số lượng sản phẩm tối thiểu phải là 1")
    Integer quantity;

    @Valid
    List<ToppingItemRequest> toppingItems = new ArrayList<>();
}
