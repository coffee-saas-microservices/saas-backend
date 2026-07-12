package org.mss301.orderservice.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mss301.orderservice.entity.enumeration.ToppingPerOrderItemStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToppingPerOrderItemResponse {

    Long toppingPerOrderItemId;
    int quantity;
    Long price;
    ToppingPerOrderItemStatus status;
    String toppingName;
}
