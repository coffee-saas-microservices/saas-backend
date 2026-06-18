package org.mss301.inventoryservice.query.model;

import lombok.Data;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;

import java.util.UUID;

@Data
public class RawIngredientResponse {
    private String id;
    private Long shopId;
    private String name;
    private BaseUnit baseUnit;
    private InventoryStatus status;
}