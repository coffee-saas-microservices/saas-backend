package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.commonservice.exception.BusinessException;
import org.springframework.util.StringUtils;
import vdhxi.catalogservice.dto.filter.CategoryFilter;
import vdhxi.catalogservice.dto.request.CategoryRequest;
import vdhxi.catalogservice.dto.response.CategoryResponse;
import vdhxi.catalogservice.entity.Category;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.CategoryMapper;
import vdhxi.catalogservice.repository.CategoryRepository;

import vdhxi.catalogservice.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên danh mục không được để trống");
        }
        Long shopId = TenantContext.getCurrentShopId();
        Category entity = new Category();
        entity.setShopId(shopId);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên danh mục không được để trống");
        }
        Category entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy danh mục"));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public CategoryResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new BusinessException("Không tìm thấy danh mục"));
    }

    public Page<CategoryResponse> getAll(CategoryFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        return repository.findByFilter(shopId, filter.getSearch(), filter.toPageable())
                .map(mapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Category entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy danh mục"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}

