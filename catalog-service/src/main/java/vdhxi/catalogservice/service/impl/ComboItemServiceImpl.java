package vdhxi.catalogservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mss301.commonservice.exception.BusinessException;
import org.springframework.util.StringUtils;
import vdhxi.catalogservice.dto.filter.ComboItemFilter;
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
        if (request == null) {
            throw new BusinessException("Yêu cầu không được để trống");
        }
        if (request.getProductId() == null) {
            throw new BusinessException("Sản phẩm không được để trống");
        }
        if (request.getProductVariantId() == null) {
            throw new BusinessException("Phiên bản sản phẩm không được để trống");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException("Tên món combo không được để trống");
        }
        if (request.getTotalPrice() == null || request.getTotalPrice() < 0) {
            throw new BusinessException("Giá combo không hợp lệ");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy sản phẩm"));
        ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiên bản sản phẩm"));
        
        Topping topping = null;
        if (request.getToppingId() != null) {
            topping = toppingRepository.findById(request.getToppingId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy topping"));
        }

        ComboItem entity = new ComboItem();
        entity.setProduct(product);
        entity.setProductVariant(variant);
        entity.setTopping(topping);
        entity.setStatus(Status.ACTIVE);
        mapper.updateEntity(entity, request);

        return mapper.toResponse(repository.save(entity));
    }

    public Page<ComboItemResponse> getByProductId(ComboItemFilter filter) {
        return repository.findByProductId(filter.getProductId(), filter.toPageable())
                .map(mapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        ComboItem entity = repository.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy combo item"));
        entity.setStatus(Status.DELETED);
        repository.save(entity);
    }
}
