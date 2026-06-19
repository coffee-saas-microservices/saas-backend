package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.request.ComboItemRequest;
import vdhxi.catalogservice.dto.response.ComboItemResponse;
import java.util.List;

public interface ComboItemService {
    ComboItemResponse create(ComboItemRequest request);
    List<ComboItemResponse> getByProductId(Long productId);
    void delete(Long id);
}
