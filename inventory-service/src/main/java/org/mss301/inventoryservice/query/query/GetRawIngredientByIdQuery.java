package org.mss301.inventoryservice.query.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GetRawIngredientByIdQuery {
    private String id;
}
