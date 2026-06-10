package org.mss301.employeeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.employeeservice.dto.request.ShiftTemplateFilter;
import org.mss301.employeeservice.dto.request.ShiftTemplateRequest;
import org.mss301.employeeservice.dto.response.ShiftTemplateResponse;
import org.mss301.employeeservice.entity.ShiftTemplate;
import org.mss301.employeeservice.entity.enumeration.ShiftTemplateStatus;
import org.mss301.employeeservice.mapper.ShiftTemplateMapper;
import org.mss301.employeeservice.repository.ShiftTemplateRepository;
import org.mss301.employeeservice.service.ShiftTemplateService;
import org.mss301.employeeservice.specification.ShiftTemplateSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShiftTemplateServiceImpl implements ShiftTemplateService {

    private final ShiftTemplateRepository shiftTemplateRepository;
    private final ShiftTemplateMapper shiftTemplateMapper;

    @Override
    @Transactional
    public ShiftTemplateResponse create(ShiftTemplateRequest request) {
        Long shopId = requireShopId();
        if (shiftTemplateRepository.existsByShopIdAndName(shopId, request.getName())) {
            throw new BusinessException("Ca làm việc '" + request.getName() + "' đã tồn tại trong cửa hàng");
        }

        ShiftTemplate shiftTemplate = shiftTemplateMapper.toEntity(request);
        shiftTemplate.setShopId(shopId);
        return shiftTemplateMapper.toResponse(shiftTemplateRepository.save(shiftTemplate));
    }

    @Override
    @Transactional
    public ShiftTemplateResponse update(Long id, ShiftTemplateRequest request) {
        Long shopId = requireShopId();
        ShiftTemplate shiftTemplate = findActiveByIdAndShop(id, shopId);
        if (!shiftTemplate.getName().equals(request.getName())
                && shiftTemplateRepository.existsByShopIdAndName(shopId, request.getName())) {
            throw new BusinessException("Ca làm việc '" + request.getName() + "' đã tồn tại trong cửa hàng");
        }

        shiftTemplateMapper.updateFromRequest(request, shiftTemplate);
        return shiftTemplateMapper.toResponse(shiftTemplateRepository.save(shiftTemplate));
    }

    @Override
    public ShiftTemplateResponse getById(Long id) {
        Long shopId = requireShopId();
        ShiftTemplate shiftTemplate = findByIdAndShop(id, shopId);
        return shiftTemplateMapper.toResponse(shiftTemplate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long shopId = requireShopId();
        ShiftTemplate shiftTemplate = findActiveByIdAndShop(id, shopId);
        shiftTemplate.setStatus(ShiftTemplateStatus.INACTIVE);
        shiftTemplateRepository.save(shiftTemplate);
    }

    @Override
    public PageResponse<ShiftTemplateResponse> getAll(ShiftTemplateFilter filter) {
        Long shopId = requireShopId();
        Specification<ShiftTemplate> spec = ShiftTemplateSpecification.filter(shopId, filter);
        Page<ShiftTemplateResponse> page = shiftTemplateRepository
                .findAll(spec, filter.toPageable())
                .map(shiftTemplateMapper::toResponse);
        return PageResponse.of(page);
    }

    private ShiftTemplate findByIdAndShop(Long id, Long shopId) {
        ShiftTemplate shiftTemplate = shiftTemplateRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy ca làm việc"));

        if (!shiftTemplate.getShopId().equals(shopId)) {
            throw new BusinessException("Bạn không có quyền truy cập ca làm việc này");
        }

        return shiftTemplate;
    }

    private ShiftTemplate findActiveByIdAndShop(Long id, Long shopId) {
        ShiftTemplate shiftTemplate = findByIdAndShop(id, shopId);
        if (ShiftTemplateStatus.INACTIVE.equals(shiftTemplate.getStatus())) {
            throw new BusinessException("Ca làm việc này đã bị vô hiệu hóa");
        }

        return shiftTemplate;
    }

    private Long requireShopId() {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("Không tìm thấy thông tin cửa hàng");
        }
        return shopId;
    }
}

