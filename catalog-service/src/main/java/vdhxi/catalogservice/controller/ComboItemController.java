package vdhxi.catalogservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vdhxi.catalogservice.common.dto.response.ApiResponse;
import vdhxi.catalogservice.common.dto.response.PageMeta;
import vdhxi.catalogservice.dto.filter.ComboItemFilter;
import vdhxi.catalogservice.dto.request.ComboItemRequest;
import vdhxi.catalogservice.dto.response.ComboItemResponse;
import vdhxi.catalogservice.service.ComboItemService;

import java.util.List;

@RestController
@RequestMapping("/api/combo-items")
@RequiredArgsConstructor
public class ComboItemController {
    private final ComboItemService service;

    @PostMapping
    public ApiResponse<ComboItemResponse> create(@RequestBody ComboItemRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, "Success", service.create(request), null);
    }

    @GetMapping
    public ApiResponse<List<ComboItemResponse>> getByProductId(@ModelAttribute ComboItemFilter filter) {
        Page<ComboItemResponse> responses = service.getByProductId(filter);

        PageMeta meta = PageMeta.builder()
                .currentPage(responses.getNumber() + 1)
                .size(responses.getSize())
                .lastPage(responses.getTotalPages())
                .totalElements(responses.getTotalElements())
                .build();

        return ApiResponse.success(HttpStatus.OK, "Get combo items successfully", responses.getContent(), meta);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(HttpStatus.OK, "Success", null, null);
    }
}

