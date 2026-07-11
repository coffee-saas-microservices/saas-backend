package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.ComboItemFilter;
import vdhxi.catalogservice.dto.request.ComboItemRequest;
import vdhxi.catalogservice.dto.response.ComboItemResponse;
import java.util.List;

public interface ComboItemService {
    ComboItemResponse create(ComboItemRequest request);
    Page<ComboItemResponse> getByProductId(ComboItemFilter filter);
    void delete(Long id);
}

