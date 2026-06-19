package vdhxi.catalogservice.service;

import vdhxi.catalogservice.dto.request.CategoryRequest;
import vdhxi.catalogservice.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll();
    void delete(Long id);
}
