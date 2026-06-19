package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.request.SizeRequest;
import vdhxi.catalogservice.dto.response.SizeResponse;
import java.util.List;

public interface SizeService {
    SizeResponse create(SizeRequest request);
    SizeResponse update(Long id, SizeRequest request);
    SizeResponse getById(Long id);
    List<SizeResponse> getAll();
    void delete(Long id);
}
