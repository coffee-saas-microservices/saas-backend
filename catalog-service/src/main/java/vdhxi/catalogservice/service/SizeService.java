package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.SizeFilter;
import vdhxi.catalogservice.dto.request.SizeRequest;
import vdhxi.catalogservice.dto.response.SizeResponse;
import java.util.List;

public interface SizeService {
    SizeResponse create(SizeRequest request);
    SizeResponse update(Long id, SizeRequest request);
    SizeResponse getById(Long id);
    Page<SizeResponse> getAll(SizeFilter filter);
    void delete(Long id);
}
