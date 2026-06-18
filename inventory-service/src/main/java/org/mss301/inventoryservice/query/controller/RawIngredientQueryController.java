package org.mss301.inventoryservice.query.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.inventoryservice.query.model.RawIngredientResponse;
import org.mss301.inventoryservice.query.query.GetAllRawIngredientsQuery;
import org.mss301.inventoryservice.query.query.GetRawIngredientByIdQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/raw-ingredients")
@RequiredArgsConstructor
public class RawIngredientQueryController {

    private final QueryGateway queryGateway;

    @GetMapping
    public ResponseEntity<PageResponse<RawIngredientResponse>> getAllRawIngredients(GetAllRawIngredientsQuery query) {
        PageResponse<RawIngredientResponse> response =
                queryGateway.query(query, ResponseTypes.instanceOf(PageResponse.class)).join();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawIngredientResponse> getRawIngredientById(@PathVariable String id) {
        GetRawIngredientByIdQuery query = new GetRawIngredientByIdQuery(id);
        RawIngredientResponse response = queryGateway.query(
                query, ResponseTypes.instanceOf(RawIngredientResponse.class)).join();
        return ResponseEntity.ok(response);
    }
}
