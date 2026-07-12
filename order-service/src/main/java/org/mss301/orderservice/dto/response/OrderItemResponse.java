package org.mss301.orderservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.OrderItemStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {

    Long orderItemId;
    Long productVariantId;
    String productName;
    String sizeName;
    Long unitPrice;
    Integer quantity;
    OrderItemStatus orderItemStatus;
    List<ToppingPerOrderItemResponse> toppingPerOrderItems;
}
