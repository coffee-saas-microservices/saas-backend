package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.ProductVariantFilter;
import vdhxi.catalogservice.dto.request.ProductVariantRequest;
import vdhxi.catalogservice.dto.response.ProductVariantResponse;
import java.util.List;

public interface ProductVariantService {
    ProductVariantResponse create(ProductVariantRequest request);
    ProductVariantResponse update(Long id, ProductVariantRequest request);
    ProductVariantResponse getById(Long id);
    Page<ProductVariantResponse> getAll(ProductVariantFilter filter);
    void delete(Long id);
}
