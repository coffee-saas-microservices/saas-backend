package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.commonservice.exception.BusinessException;
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
        if (request == null) {
            throw new BusinessException("Yêu cầu không được để trống");
        }
        if (request.getRawIngredientId() == null) {
            throw new BusinessException("Nguyên liệu thô không được để trống");
        }
        if (request.getQuantityRequired() == null || request.getQuantityRequired() <= 0) {
            throw new BusinessException("Số lượng yêu cầu phải lớn hơn 0");
        }
        if (request.getProductVariantId() == null && request.getToppingId() == null) {
            throw new BusinessException("Công thức phải liên kết với phiên bản sản phẩm hoặc topping");
        }
        if (request.getProductVariantId() != null && request.getToppingId() != null) {
            throw new BusinessException("Công thức chỉ có thể liên kết với phiên bản sản phẩm hoặc topping, không thể cả hai");
        }

        Long shopId = TenantContext.getCurrentShopId();
        Recipe entity = new Recipe();
        entity.setShopId(shopId);
        entity.setRawIngredientId(request.getRawIngredientId());
        
        if (request.getProductVariantId() != null) {
            ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy phiên bản sản phẩm"));
            entity.setProductVariant(variant);
        }
        
        if (request.getToppingId() != null) {
            Topping topping = toppingRepository.findById(request.getToppingId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy topping"));
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
        Recipe entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy công thức"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
