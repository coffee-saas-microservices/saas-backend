package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.ToppingFilter;
import vdhxi.catalogservice.dto.request.ToppingRequest;
import vdhxi.catalogservice.dto.response.ToppingResponse;
import java.util.List;

public interface ToppingService {
    ToppingResponse create(ToppingRequest request);
    ToppingResponse update(Long id, ToppingRequest request);
    ToppingResponse getById(Long id);
    Page<ToppingResponse> getAll(ToppingFilter filter);
    void delete(Long id);
}
