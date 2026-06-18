package org.mss301.inventoryservice.query.projection;

import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.inventoryservice.command.data.entity.RawIngredient;
import org.mss301.inventoryservice.command.data.repository.RawIngredientRepository;
import org.mss301.inventoryservice.query.model.RawIngredientResponse;
import org.mss301.inventoryservice.query.query.GetAllRawIngredientsQuery;
import org.mss301.inventoryservice.query.query.GetRawIngredientByIdQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RawIngredientProjection {

    private final RawIngredientRepository repository;

    @QueryHandler
    public PageResponse<RawIngredientResponse> handle(GetAllRawIngredientsQuery query) {
        Page<RawIngredient> page = repository.findAll(PageRequest.of(query.getPage() - 1, query.getPageSize()));
        List<RawIngredientResponse> responses = page.getContent().stream().map(entity -> {
            RawIngredientResponse response = new RawIngredientResponse();
            BeanUtils.copyProperties(entity, response);
            return response;
        }).collect(Collectors.toList());

        return PageResponse.<RawIngredientResponse>builder()
                .content(responses)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @QueryHandler
    public RawIngredientResponse handle(GetRawIngredientByIdQuery query) {
        RawIngredient entity = repository.findById(query.getId())
                .orElseThrow(() -> new RuntimeException("Raw Ingredient not found"));
        RawIngredientResponse response = new RawIngredientResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
