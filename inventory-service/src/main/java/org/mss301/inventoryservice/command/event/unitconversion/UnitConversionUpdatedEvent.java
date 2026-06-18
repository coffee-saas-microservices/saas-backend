package org.mss301.inventoryservice.command.event.unitconversion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InputUnit;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitConversionUpdatedEvent {
    String id;
    String ingredientId;
    InputUnit fromUnit;
    BaseUnit toUnit;
    Double conversionFactor;
    Boolean isStandard;
    Long shopId;
}
