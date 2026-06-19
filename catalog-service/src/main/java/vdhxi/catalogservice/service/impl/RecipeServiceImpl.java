package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.common.multitenancy.TenantContext;
import vdhxi.catalogservice.dto.request.RecipeRequest;
import vdhxi.catalogservice.dto.response.RecipeResponse;
import vdhxi.catalogservice.entity.ProductVariant;
import vdhxi.catalogservice.entity.Recipe;
import vdhxi.catalogservice.entity.Topping;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.RecipeMapper;
import vdhxi.catalogservice.repository.ProductVariantRepository;
import vdhxi.catalogservice.repository.RecipeRepository;
import vdhxi.catalogservice.repository.ToppingRepository;

import java.util.List;
import java.util.stream.Collectors;

import vdhxi.catalogservice.service.RecipeService;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository repository;
    private final ProductVariantRepository variantRepository;
    private final ToppingRepository toppingRepository;
    private final RecipeMapper mapper;

    @Transactional
    public RecipeResponse create(RecipeRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        
        Recipe entity = new Recipe();
        entity.setShopId(shopId);
        entity.setRawIngredientId(request.getRawIngredientId());
        
        if (request.getProductVariantId() != null) {
            ProductVariant variant = variantRepository.findById(request.getProductVariantId()).orElseThrow(() -> new RuntimeException("Variant not found"));
            entity.setProductVariant(variant);
        }
        
        if (request.getToppingId() != null) {
            Topping topping = toppingRepository.findById(request.getToppingId()).orElseThrow(() -> new RuntimeException("Topping not found"));
            entity.setTopping(topping);
        }

        entity.setQuantityRequired(request.getQuantityRequired());
        entity.setNote(request.getNote());
        entity.setStatus(Status.ACTIVE);

        return mapper.toResponse(repository.save(entity));
    }

    public List<RecipeResponse> getByProductVariant(Long variantId) {
        return repository.findByProductVariantId(variantId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Recipe entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
