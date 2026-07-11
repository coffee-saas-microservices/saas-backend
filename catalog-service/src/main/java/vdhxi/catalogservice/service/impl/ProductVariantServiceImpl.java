package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.commonservice.exception.BusinessException;
import org.springframework.util.StringUtils;
import vdhxi.catalogservice.dto.filter.ProductVariantFilter;
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
        if (request == null) {
            throw new BusinessException("Yêu cầu không được để trống");
        }
        if (request.getProductId() == null) {
            throw new BusinessException("Sản phẩm không được để trống");
        }
        if (request.getSizeId() == null) {
            throw new BusinessException("Kích thước không được để trống");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            throw new BusinessException("Giá sản phẩm không hợp lệ");
        }
        if (!StringUtils.hasText(request.getCode())) {
            throw new BusinessException("Mã phiên bản không được để trống");
        }

        Long shopId = TenantContext.getCurrentShopId();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy sản phẩm"));
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy kích thước"));

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
        if (request == null) {
            throw new BusinessException("Yêu cầu không được để trống");
        }
        if (request.getPrice() != null && request.getPrice() < 0) {
            throw new BusinessException("Giá sản phẩm không hợp lệ");
        }
        ProductVariant entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy phiên bản sản phẩm"));
        
        if (request.getSizeId() != null && !request.getSizeId().equals(entity.getSize().getId())) {
            Size size = sizeRepository.findById(request.getSizeId()).orElseThrow(() -> new BusinessException("Không tìm thấy kích thước"));
            entity.setSize(size);
        }
        
        mapper.updateEntity(entity, request);
        return mapper.toResponse(repository.save(entity));
    }

    public ProductVariantResponse getById(Long id) {
        return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new BusinessException("Không tìm thấy phiên bản sản phẩm"));
    }

    public Page<ProductVariantResponse> getAll(ProductVariantFilter filter) {
        Long shopId = TenantContext.getCurrentShopId();
        return repository.findByFilter(shopId, filter.getProductId(), filter.getSearch(), filter.toPageable())
                .map(mapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        ProductVariant entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy phiên bản sản phẩm"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}

