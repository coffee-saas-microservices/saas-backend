package org.mss301.inventoryservice.query.controller;

import jdk.jfr.Percentage;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.mss301.inventoryservice.command.data.enumeration.InventoryStatus;
import org.mss301.inventoryservice.query.model.UnitConversionResponse;
import org.mss301.inventoryservice.query.query.GetAllUnitConversionQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/unit-conversions")
@RequiredArgsConstructor
public class UnitConversionQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public List<UnitConversionResponse> getAllByIngredientId(
            @RequestParam(required = false) UUID ingredientId,
            @RequestParam(required = false, defaultValue = "ACTIVE") InventoryStatus status
            ) {

        GetAllUnitConversionQuery query = new GetAllUnitConversionQuery(ingredientId,  status);

        return queryGateway.query(query, ResponseTypes.multipleInstancesOf(UnitConversionResponse.class)).join();
    }
}
