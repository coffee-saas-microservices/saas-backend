package org.mss301.inventoryservice.service.inter;

import org.mss301.inventoryservice.dto.request.UnitConversionRequest;
import org.mss301.inventoryservice.dto.response.UnitConversionResponse;
import org.mss301.inventoryservice.entity.UnitConversion;
import org.mss301.inventoryservice.entity.enumeration.InputUnit;

public interface UnitConversionService {
    UnitConversionResponse create(UnitConversionRequest request);
    UnitConversion update(Long id, UnitConversionRequest request);
    Double convertToBaseUnit(Long ingredientId, InputUnit fromUnit, Double quantity);
}
