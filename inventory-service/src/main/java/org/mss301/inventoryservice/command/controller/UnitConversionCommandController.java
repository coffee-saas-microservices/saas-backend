package org.mss301.inventoryservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.mss301.inventoryservice.command.command.CreateUnitConversionCommand;
import org.mss301.inventoryservice.command.model.request.UnitConversionRequest;
import org.mss301.inventoryservice.command.model.response.UnitConversionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/unit-conversions")
@RequiredArgsConstructor
public class UnitConversionCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody UnitConversionRequest request) {
        UUID aggregateId = UUID.randomUUID();

        Long shopId = 1L;

        CreateUnitConversionCommand command = new CreateUnitConversionCommand(
                aggregateId,
                shopId,
                request.getIngredientId(),
                request.getFromUnit(),
                request.getToUnit(),
                request.getConversionFactor(),
                request.getIsStandard()
        );

        commandGateway.sendAndWait(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(aggregateId);
    }
}
