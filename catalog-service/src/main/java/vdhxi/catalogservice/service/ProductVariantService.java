package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.request.ProductVariantRequest;
import vdhxi.catalogservice.dto.response.ProductVariantResponse;
import java.util.List;

public interface ProductVariantService {
    ProductVariantResponse create(ProductVariantRequest request);
    ProductVariantResponse update(Long id, ProductVariantRequest request);
    ProductVariantResponse getById(Long id);
    List<ProductVariantResponse> getAllByProduct(Long productId);
    void delete(Long id);
}
