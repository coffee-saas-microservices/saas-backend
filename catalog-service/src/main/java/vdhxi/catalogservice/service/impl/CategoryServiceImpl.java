package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.common.multitenancy.TenantContext;
import vdhxi.catalogservice.dto.request.CategoryRequest;
import vdhxi.catalogservice.dto.response.CategoryResponse;
import vdhxi.catalogservice.entity.Category;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.CategoryMapper;
import vdhxi.catalogservice.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import vdhxi.catalogservice.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        Category entity = new Category();
        entity.setShopId(shopId);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public CategoryResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public List<CategoryResponse> getAll() {
        Long shopId = TenantContext.getCurrentShopId();
        return repository.findByShopId(shopId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Category entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
