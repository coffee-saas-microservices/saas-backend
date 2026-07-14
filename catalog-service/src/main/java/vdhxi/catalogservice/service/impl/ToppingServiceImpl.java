package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.commonservice.exception.BusinessException;
import org.springframework.util.StringUtils;
import vdhxi.catalogservice.dto.filter.ToppingFilter;
import vdhxi.catalogservice.dto.request.ToppingRequest;
import vdhxi.catalogservice.dto.response.ToppingResponse;
import vdhxi.catalogservice.entity.Topping;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.ToppingMapper;
import vdhxi.catalogservice.repository.ToppingRepository;

import vdhxi.catalogservice.service.ToppingService;

@Service
@RequiredArgsConstructor
public class ToppingServiceImpl implements ToppingService {
    private final ToppingRepository repository;
    private final ToppingMapper mapper;

    @Transactional
    public ToppingResponse create(ToppingRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên topping không được để trống");
        }
        Long shopId = TenantContext.getCurrentShopId();
        Topping entity = new Topping();
        entity.setShopId(shopId);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public ToppingResponse update(Long id, ToppingRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên topping không được để trống");
        }
        Topping entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy topping"));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public ToppingResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new BusinessException("Không tìm thấy topping"));
    }

    public Page<ToppingResponse> getAll(ToppingFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        return repository.findByFilter(shopId, filter.getSearch(), filter.toPageable())
                .map(mapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Topping entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy topping"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}

