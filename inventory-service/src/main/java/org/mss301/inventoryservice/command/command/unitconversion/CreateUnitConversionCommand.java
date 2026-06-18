package org.mss301.inventoryservice.command.command.unitconversion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.mss301.inventoryservice.command.data.enumeration.BaseUnit;
import org.mss301.inventoryservice.command.data.enumeration.InputUnit;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUnitConversionCommand {

    @TargetAggregateIdentifier
    UUID id;
    Long shopId;
    UUID ingredientId;
    InputUnit fromUnit;
    BaseUnit toUnit;
    Double conversionFactor;
    Boolean isStandard;
}
