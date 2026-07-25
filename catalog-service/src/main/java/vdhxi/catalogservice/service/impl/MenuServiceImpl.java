package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.dto.response.menu.MenuCategoryResponse;
import vdhxi.catalogservice.dto.response.menu.MenuProductResponse;
import vdhxi.catalogservice.dto.response.menu.MenuToppingResponse;
import vdhxi.catalogservice.dto.response.menu.MenuVariantResponse;
import vdhxi.catalogservice.entity.Category;
import vdhxi.catalogservice.entity.Product;
import vdhxi.catalogservice.entity.ProductAllowedTopping;
import vdhxi.catalogservice.entity.ProductVariant;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.repository.CategoryRepository;
import vdhxi.catalogservice.repository.ProductAllowedToppingRepository;
import vdhxi.catalogservice.repository.ProductRepository;
import vdhxi.catalogservice.repository.ProductVariantRepository;
import vdhxi.catalogservice.service.MenuService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductAllowedToppingRepository productAllowedToppingRepository;

    @Override
    public List<MenuCategoryResponse> getFullMenu() {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("Không xác định được cửa hàng");
        }

        List<Category> activeCategories = categoryRepository.findByShopIdAndStatus(shopId, Status.ACTIVE);

        return activeCategories.stream()
                .map(this::buildCategoryResponse)
                .filter(cat -> !cat.getProducts().isEmpty()) // ẩn category không có sản phẩm
                .collect(Collectors.toList());
    }

    @Override
    public MenuCategoryResponse getMenuByCategory(Long categoryId) {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("Không xác định được cửa hàng");
        }

        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getShopId().equals(shopId))
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new BusinessException("Danh mục không tồn tại hoặc không khả dụng"));

        return buildCategoryResponse(category);
    }

    // ──────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────

    private MenuCategoryResponse buildCategoryResponse(Category category) {
        List<Product> products = productRepository
                .findByCategoryIdAndStatusOrderByName(category.getId(), Status.ACTIVE);

        List<MenuProductResponse> productResponses = products.stream()
                .map(this::buildProductResponse)
                .collect(Collectors.toList());

        return MenuCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .products(productResponses)
                .build();
    }

    private MenuProductResponse buildProductResponse(Product product) {
        // Lấy variants còn bán (ACTIVE hoặc OUTOFSTOCK - khách cần thấy để biết có size nào)
        List<ProductVariant> variants = productVariantRepository
                .findByProductIdAndStatusOrderByPrice(product.getId(), Status.ACTIVE);

        List<MenuVariantResponse> variantResponses = variants.stream()
                .map(v -> MenuVariantResponse.builder()
                        .id(v.getId())
                        .sizeName(v.getSize().getName())
                        .price(v.getPrice())
                        .code(v.getCode())
                        .build())
                .collect(Collectors.toList());

        // Lấy toppings được phép cho sản phẩm này (chỉ topping ACTIVE)
        List<ProductAllowedTopping> allowedToppings = productAllowedToppingRepository
                .findByProductId(product.getId());

        List<MenuToppingResponse> toppingResponses = allowedToppings.stream()
                .filter(at -> at.getTopping().getStatus() == Status.ACTIVE)
                .map(at -> MenuToppingResponse.builder()
                        .id(at.getTopping().getId())
                        .name(at.getTopping().getName())
                        .price(at.getTopping().getPrice())
                        .build())
                .collect(Collectors.toList());

        return MenuProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .variants(variantResponses)
                .allowedToppings(toppingResponses)
                .build();
    }
}
