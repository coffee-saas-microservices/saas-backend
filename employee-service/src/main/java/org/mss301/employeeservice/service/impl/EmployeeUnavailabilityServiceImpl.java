package org.mss301.employeeservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.employeeservice.client.IdentityServiceClient;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityFilter;
import org.mss301.employeeservice.dto.request.EmployeeUnavailabilityRequest;
import org.mss301.employeeservice.dto.response.EmployeeUnavailabilityResponse;
import org.mss301.employeeservice.dto.response.UserResponse;
import org.mss301.employeeservice.entity.Employee;
import org.mss301.employeeservice.entity.EmployeeUnavailability;
import org.mss301.employeeservice.entity.enumeration.UnavailabilityStatus;
import org.mss301.employeeservice.mapper.EmployeeUnavailabilityMapper;
import org.mss301.employeeservice.repository.EmployeeRepository;
import org.mss301.employeeservice.repository.EmployeeUnavailabilityRepository;
import org.mss301.employeeservice.service.EmployeeUnavailabilityService;
import org.mss301.employeeservice.specification.EmployeeUnavailabilitySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeUnavailabilityServiceImpl implements EmployeeUnavailabilityService {

    private final EmployeeUnavailabilityRepository unavailabilityRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeUnavailabilityMapper mapper;
    private final IdentityServiceClient identityServiceClient;

    @Override
    @Transactional
    public EmployeeUnavailabilityResponse create(EmployeeUnavailabilityRequest request) {
        if (request.getEmployeeId() == null) {
            throw new BusinessException("employeeId không được để trống");
        }

        Long shopId = requireShopId();

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));

        if (!employee.getShopId().equals(shopId)) {
            throw new BusinessException("Nhân viên không thuộc cửa hàng này");
        }

        if (request.getStartTime() != null && request.getEndTime() != null
                && request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        EmployeeUnavailability entity = mapper.toEntity(request);
        entity.setEmployee(employee);
        entity.setShopId(shopId);

        if (request.getStartTime() != null) {
            entity.setDayOfWeek(request.getStartTime().getDayOfWeek());
        } else if (request.getSpecificDate() != null) {
            entity.setDayOfWeek(request.getSpecificDate().getDayOfWeek());
        }

        EmployeeUnavailability saved = unavailabilityRepository.save(entity);
        EmployeeUnavailabilityResponse response = mapper.toResponse(saved);
        enrichWithEmployeeName(response, employee.getUserId());
        return response;
    }

    @Override
    @Transactional
    public EmployeeUnavailabilityResponse update(Long id, EmployeeUnavailabilityRequest request) {
        Long shopId = requireShopId();

        EmployeeUnavailability entity = unavailabilityRepository.findByEmployeeUnavailabilityIdAndShopId(id, shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin bận của nhân viên"));

        if (request.getStartTime() != null && request.getEndTime() != null
                && request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        mapper.updateFromRequest(request, entity);

        if (request.getEmployeeId() != null && !request.getEmployeeId().equals(entity.getEmployee().getEmployeeId())) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));
            if (!employee.getShopId().equals(shopId)) {
                throw new BusinessException("Nhân viên không thuộc cửa hàng này");
            }
            entity.setEmployee(employee);
        }

        if (request.getStartTime() != null) {
            entity.setDayOfWeek(request.getStartTime().getDayOfWeek());
        } else if (request.getSpecificDate() != null) {
            entity.setDayOfWeek(request.getSpecificDate().getDayOfWeek());
        }

        EmployeeUnavailability saved = unavailabilityRepository.save(entity);
        EmployeeUnavailabilityResponse response = mapper.toResponse(saved);
        enrichWithEmployeeName(response, entity.getEmployee().getUserId());
        return response;
    }

    @Override
    public EmployeeUnavailabilityResponse getDetail(Long id) {
        Long shopId = requireShopId();

        EmployeeUnavailability entity = unavailabilityRepository.findByEmployeeUnavailabilityIdAndShopId(id, shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin bận của nhân viên"));

        EmployeeUnavailabilityResponse response = mapper.toResponse(entity);
        enrichWithEmployeeName(response, entity.getEmployee().getUserId());
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long shopId = requireShopId();

        EmployeeUnavailability entity = unavailabilityRepository.findByEmployeeUnavailabilityIdAndShopId(id, shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy thông tin bận của nhân viên"));

        entity.setStatus(UnavailabilityStatus.INACTIVE);
        unavailabilityRepository.save(entity);
    }

    @Override
    public PageResponse<EmployeeUnavailabilityResponse> getAll(EmployeeUnavailabilityFilter filter) {
        Long shopId = requireShopId();
        Specification<EmployeeUnavailability> spec = EmployeeUnavailabilitySpecification.filter(shopId, filter);

        Page<EmployeeUnavailability> page = unavailabilityRepository.findAll(spec, filter.toPageable());

        Page<EmployeeUnavailabilityResponse> responsePage = page.map(entity -> {
            EmployeeUnavailabilityResponse res = mapper.toResponse(entity);
            if (entity.getEmployee() != null) {
                enrichWithEmployeeName(res, entity.getEmployee().getUserId());
            }
            return res;
        });

        return PageResponse.of(responsePage);
    }

    private void enrichWithEmployeeName(EmployeeUnavailabilityResponse response, Long userId) {
        if (userId != null) {
            try {
                String domain = getCurrentDomain();
                UserResponse user = identityServiceClient.getUserById(userId, domain).getBody();
                if (user != null) {
                    response.setEmployeeName(user.getFullname());
                }
            } catch (Exception e) {
                log.error("Lỗi khi lấy thông tin user profile từ identity-service: ", e);
            }
        }
    }

    private String getCurrentDomain() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String host = request.getHeader("X-Forwarded-Host");
            if (host == null || host.isEmpty()) {
                host = request.getHeader("Host");
            }
            return host;
        }
        return null;
    }

    private Long requireShopId() {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) {
            throw new BusinessException("Không tìm thấy thông tin cửa hàng");
        }
        return shopId;
    }
}
