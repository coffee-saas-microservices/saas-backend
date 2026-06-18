package org.mss301.inventoryservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;
import org.mss301.inventoryservice.command.data.repository.UnitConversionRepository;
import org.mss301.inventoryservice.query.model.UnitConversionResponse;
import org.mss301.inventoryservice.query.query.GetAllUnitConversionQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UnitConversionProjection {

    private final UnitConversionRepository unitConversionRepository;

    @QueryHandler
    public List<UnitConversionResponse> handle(GetAllUnitConversionQuery query) {
        return unitConversionRepository.findAllByIngredientIdAndInventoryStatus(query.getIngredientId(), InventoryStatus.ACTIVE)
                .stream()
                .map(unitConversion -> new UnitConversionResponse(
                        unitConversion.getId(),
                        unitConversion.getIngredient().getId(),
                        unitConversion.getShopId(),
                        unitConversion.getIngredient().getName(),
                        unitConversion.getFromUnit(),
                        unitConversion.getToUnit(),
                        unitConversion.getConversionFactor(),
                        unitConversion.getIsStandard(),
                        unitConversion.getInventoryStatus(),
                        unitConversion.getCreatedAt(),
                        unitConversion.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}
