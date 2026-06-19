package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vdhxi.catalogservice.common.multitenancy.TenantContext;
import vdhxi.catalogservice.dto.request.ProductRequest;
import vdhxi.catalogservice.dto.response.ProductResponse;
import vdhxi.catalogservice.entity.Category;
import vdhxi.catalogservice.entity.Product;
import vdhxi.catalogservice.entity.ProductAllowedTopping;
import vdhxi.catalogservice.entity.Topping;
import vdhxi.catalogservice.enums.Status;
import vdhxi.catalogservice.mapper.ProductMapper;
import vdhxi.catalogservice.mapper.ToppingMapper;
import vdhxi.catalogservice.repository.CategoryRepository;
import vdhxi.catalogservice.repository.ProductAllowedToppingRepository;
import vdhxi.catalogservice.repository.ProductRepository;
import vdhxi.catalogservice.repository.ToppingRepository;

import java.util.List;
import java.util.stream.Collectors;

import vdhxi.catalogservice.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductAllowedToppingRepository allowedToppingRepository;
    private final ToppingRepository toppingRepository;
    private final ProductMapper productMapper;
    private final ToppingMapper toppingMapper;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setShopId(shopId);
        product.setCategory(category);
        product.setStatus(Status.ACTIVE);
        productMapper.updateEntity(product, request);

        product = productRepository.save(product);

        if (request.getAllowedToppingIds() != null) {
            updateAllowToppings(product.getId(), request.getAllowedToppingIds());
        }

        return getById(product.getId());
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (request.getCategoryId() != null && !request.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        productMapper.updateEntity(product, request);
        productRepository.save(product);

        if (request.getAllowedToppingIds() != null) {
            updateAllowToppings(product.getId(), request.getAllowedToppingIds());
        }

        return getById(product.getId());
    }

    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductResponse response = productMapper.toResponse(product);
        
        List<ProductAllowedTopping> allowedToppings = allowedToppingRepository.findByProductId(id);
        response.setAllowedToppings(allowedToppings.stream()
                .filter(pat -> pat.getStatus() == Status.ACTIVE)
                .map(pat -> toppingMapper.toResponse(pat.getTopping()))
                .collect(Collectors.toList()));
        return response;
    }

    public List<ProductResponse> getAll() {
        Long shopId = TenantContext.getCurrentShopId();
        return productRepository.findByShopId(shopId).stream()
                .map(p -> getById(p.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(Status.DELETED);
        productRepository.save(product);
    }

    @Transactional
    public void updateAllowToppings(Long productId, List<Long> toppingIds) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        
        List<ProductAllowedTopping> existing = allowedToppingRepository.findByProductId(productId);
        allowedToppingRepository.deleteAll(existing);

        if (toppingIds != null && !toppingIds.isEmpty()) {
            List<Topping> toppings = toppingRepository.findAllById(toppingIds);
            List<ProductAllowedTopping> toSave = toppings.stream().map(t -> {
                ProductAllowedTopping pat = new ProductAllowedTopping();
                pat.setProduct(product);
                pat.setTopping(t);
                pat.setStatus(Status.ACTIVE);
                return pat;
            }).collect(Collectors.toList());
            allowedToppingRepository.saveAll(toSave);
        }
    }
}
