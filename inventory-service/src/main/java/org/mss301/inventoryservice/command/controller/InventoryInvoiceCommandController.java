package org.mss301.inventoryservice.command.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.inventoryservice.command.command.inventoryinvoice.CreateInventoryInvoiceCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/inventory-invoices")
@RequiredArgsConstructor
public class InventoryInvoiceCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<String> createInvoice(@RequestBody CreateInventoryInvoiceCommand command) {
        command.setInvoiceId(UUID.randomUUID());

        command.setShopId(TenantContext.getCurrentShopId());

        String result = commandGateway.sendAndWait(command);
        return ResponseEntity.ok(result);
    }
}
