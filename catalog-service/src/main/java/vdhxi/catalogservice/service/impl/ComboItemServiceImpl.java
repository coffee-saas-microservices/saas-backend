package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.dto.request.ComboItemRequest;
import vdhxi.catalogservice.dto.response.ComboItemResponse;
import vdhxi.catalogservice.entity.ComboItem;
import vdhxi.catalogservice.entity.Product;
import vdhxi.catalogservice.entity.ProductVariant;
import vdhxi.catalogservice.entity.Topping;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.ComboItemMapper;
import vdhxi.catalogservice.repository.ComboItemRepository;
import vdhxi.catalogservice.repository.ProductRepository;
import vdhxi.catalogservice.repository.ProductVariantRepository;
import vdhxi.catalogservice.repository.ToppingRepository;

import java.util.List;
import java.util.stream.Collectors;

import vdhxi.catalogservice.service.ComboItemService;

@Service
@RequiredArgsConstructor
public class ComboItemServiceImpl implements ComboItemService {
    private final ComboItemRepository repository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ToppingRepository toppingRepository;
    private final ComboItemMapper mapper;

    @Transactional
    public ComboItemResponse create(ComboItemRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductVariant variant = variantRepository.findById(request.getProductVariantId()).orElseThrow(() -> new RuntimeException("Variant not found"));
        
        Topping topping = null;
        if (request.getToppingId() != null) {
            topping = toppingRepository.findById(request.getToppingId()).orElseThrow(() -> new RuntimeException("Topping not found"));
        }

        ComboItem entity = new ComboItem();
        entity.setProduct(product);
        entity.setProductVariant(variant);
        entity.setTopping(topping);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    public List<ComboItemResponse> getByProductId(Long productId) {
        return repository.findByProductId(productId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        ComboItem entity = repository.findById(id).orElseThrow(() -> new RuntimeException("ComboItem not found"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
