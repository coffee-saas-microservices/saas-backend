package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.commonservice.exception.BusinessException;
import org.springframework.util.StringUtils;
import vdhxi.catalogservice.dto.filter.SizeFilter;
import vdhxi.catalogservice.dto.request.SizeRequest;
import vdhxi.catalogservice.dto.response.SizeResponse;
import vdhxi.catalogservice.entity.Size;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.SizeMapper;
import vdhxi.catalogservice.repository.SizeRepository;

import vdhxi.catalogservice.service.SizeService;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {
    private final SizeRepository repository;
    private final SizeMapper mapper;

    @Transactional
    public SizeResponse create(SizeRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên kích thước không được để trống");
        }
        Long shopId = TenantContext.getCurrentShopId();
        Size entity = new Size();
        entity.setShopId(shopId);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public SizeResponse update(Long id, SizeRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên kích thước không được để trống");
        }
        Size entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy kích thước"));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public SizeResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new BusinessException("Không tìm thấy kích thước"));
    }

    public Page<SizeResponse> getAll(SizeFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        return repository.findByFilter(shopId, filter.getSearch(), filter.toPageable())
                .map(mapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Size entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy kích thước"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}

