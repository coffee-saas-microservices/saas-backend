package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.common.multitenancy.TenantContext;
import vdhxi.catalogservice.dto.request.ProductVariantRequest;
import vdhxi.catalogservice.dto.response.ProductVariantResponse;
import vdhxi.catalogservice.entity.Product;
import vdhxi.catalogservice.entity.ProductVariant;
import vdhxi.catalogservice.entity.Size;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.ProductVariantMapper;
import vdhxi.catalogservice.repository.ProductRepository;
import vdhxi.catalogservice.repository.ProductVariantRepository;
import vdhxi.catalogservice.repository.SizeRepository;

import java.util.List;
import java.util.stream.Collectors;

import vdhxi.catalogservice.service.ProductVariantService;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository repository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ProductVariantMapper mapper;

    @Transactional
    public ProductVariantResponse create(ProductVariantRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        Size size = sizeRepository.findById(request.getSizeId()).orElseThrow(() -> new RuntimeException("Size not found"));

        ProductVariant entity = new ProductVariant();
        entity.setShopId(shopId);
        entity.setProduct(product);
        entity.setSize(size);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public ProductVariantResponse update(Long id, ProductVariantRequest request) {
        ProductVariant entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Variant not found"));
        
        if (request.getSizeId() != null && !request.getSizeId().equals(entity.getSize().getId())) {
            Size size = sizeRepository.findById(request.getSizeId()).orElseThrow(() -> new RuntimeException("Size not found"));
            entity.setSize(size);
        }
        
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public ProductVariantResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new RuntimeException("Variant not found"));
    }

    public List<ProductVariantResponse> getAllByProduct(Long productId) {
        return repository.findByProductId(productId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        ProductVariant entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Variant not found"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
