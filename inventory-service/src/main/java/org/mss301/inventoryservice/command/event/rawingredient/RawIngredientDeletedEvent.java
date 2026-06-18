package org.mss301.inventoryservice.command.event.rawingredient;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RawIngredientDeletedEvent {
    private String id;
}