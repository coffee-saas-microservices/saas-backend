package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.ProductFilter;
import vdhxi.catalogservice.dto.request.ProductRequest;
import vdhxi.catalogservice.dto.response.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    ProductResponse getById(Long id);
    Page<ProductResponse> getAll(ProductFilter filter);
    void delete(Long id);
    void updateAllowToppings(Long productId, List<Long> toppingIds);
}
