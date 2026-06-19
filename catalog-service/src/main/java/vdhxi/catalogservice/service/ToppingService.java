package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.request.ToppingRequest;
import vdhxi.catalogservice.dto.response.ToppingResponse;
import java.util.List;

public interface ToppingService {
    ToppingResponse create(ToppingRequest request);
    ToppingResponse update(Long id, ToppingRequest request);
    ToppingResponse getById(Long id);
    List<ToppingResponse> getAll();
    void delete(Long id);
}
