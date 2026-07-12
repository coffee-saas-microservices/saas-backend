package vdhxi.catalogservice.service;

import org.springframework.data.domain.Page;
import vdhxi.catalogservice.dto.filter.CategoryFilter;
import vdhxi.catalogservice.dto.request.CategoryRequest;
import vdhxi.catalogservice.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    CategoryResponse getById(Long id);
    Page<CategoryResponse> getAll(CategoryFilter filter);
    void delete(Long id);
}
