package org.mss301.inventoryservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.mss301.inventoryservice.command.command.CreateUnitConversionCommand;
import org.mss301.inventoryservice.command.command.DeleteUnitConversionCommand;
import org.mss301.inventoryservice.command.command.UpdateUnitConversionCommand;
import org.mss301.inventoryservice.command.model.UnitConversionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PutMapping("{/id}")
    public ResponseEntity<String> update(
            @PathVariable UUID id,
            @Valid @RequestBody UnitConversionRequest request
    ) {
        Long shopId = 1L;

        UpdateUnitConversionCommand command = new UpdateUnitConversionCommand(
                id,
                shopId,
                request.getIngredientId(),
                request.getFromUnit(),
                request.getToUnit(),
                request.getConversionFactor(),
                request.getIsStandard()
        );

        commandGateway.sendAndWait(command);

        return ResponseEntity.ok().body("Unit conversion updated successfully");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {

        DeleteUnitConversionCommand command = new DeleteUnitConversionCommand(id);

        commandGateway.sendAndWait(command);

        return ResponseEntity.ok().body("Unit conversion deleted successfully");
    }
}
