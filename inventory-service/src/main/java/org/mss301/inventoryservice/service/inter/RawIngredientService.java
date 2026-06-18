package org.mss301.inventoryservice.service.inter;

import org.mss301.inventoryservice.dto.filter.RawIngredientFilter;
import org.mss301.inventoryservice.dto.request.RawIngredientRequest;
import org.mss301.inventoryservice.dto.response.RawIngredientResponse;
import org.springframework.data.domain.Page;

public interface RawIngredientService {
    RawIngredientResponse create(RawIngredientRequest request);
    RawIngredientResponse update(Long id, RawIngredientRequest request);
    Page<RawIngredientResponse> getAll(RawIngredientFilter filter);
    RawIngredientResponse getDetail(Long id);
}
