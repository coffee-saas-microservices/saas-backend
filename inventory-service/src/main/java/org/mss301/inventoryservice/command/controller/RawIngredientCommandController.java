package org.mss301.inventoryservice.command.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.mss301.inventoryservice.command.command.rawingredient.CreateRawIngredientCommand;
import org.mss301.inventoryservice.command.command.rawingredient.DeleteRawIngredientCommand;
import org.mss301.inventoryservice.command.command.rawingredient.UpdateRawIngredientCommand;
import org.mss301.inventoryservice.command.model.RawIngredientRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/raw-ingredients")
@RequiredArgsConstructor
public class RawIngredientCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody RawIngredientRequest request) {
        CreateRawIngredientCommand command = CreateRawIngredientCommand.builder()
                .id(UUID.randomUUID().toString())
                .shopId(1L)
                .name(request.getName())
                .baseUnit(request.getBaseUnit())
                .status(request.getStatus())
                .build();
        String result = commandGateway.sendAndWait(command);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody RawIngredientRequest request) {
        UpdateRawIngredientCommand command = UpdateRawIngredientCommand.builder()
                .id(id)
                .name(request.getName())
                .status(request.getStatus())
                .build();
        String result = commandGateway.sendAndWait(command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        DeleteRawIngredientCommand command = DeleteRawIngredientCommand.builder()
                .id(id)
                .build();
        String result = commandGateway.sendAndWait(command);
        return ResponseEntity.ok(result);
    }
}
