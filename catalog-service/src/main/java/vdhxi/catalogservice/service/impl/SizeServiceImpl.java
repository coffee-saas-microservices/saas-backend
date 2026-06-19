package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.common.multitenancy.TenantContext;
import vdhxi.catalogservice.dto.request.SizeRequest;
import vdhxi.catalogservice.dto.response.SizeResponse;
import vdhxi.catalogservice.entity.Size;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.SizeMapper;
import vdhxi.catalogservice.repository.SizeRepository;

import java.util.List;
import java.util.stream.Collectors;

import vdhxi.catalogservice.service.SizeService;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {
    private final SizeRepository repository;
    private final SizeMapper mapper;

    @Transactional
    public SizeResponse create(SizeRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        Size entity = new Size();
        entity.setShopId(shopId);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public SizeResponse update(Long id, SizeRequest request) {
        Size entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Size not found"));
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public SizeResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new RuntimeException("Size not found"));
    }

    public List<SizeResponse> getAll() {
        Long shopId = TenantContext.getCurrentShopId();
        return repository.findByShopId(shopId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Size entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Size not found"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
