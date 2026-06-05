package org.mss301.employeeservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.employeeservice.client.IdentityServiceClient;
import org.mss301.employeeservice.dto.request.EmployeeFilter;
import org.mss301.employeeservice.dto.request.EmployeeRequest;
import org.mss301.employeeservice.dto.request.UpdateEmployeeRequest;
import org.mss301.employeeservice.dto.response.EmployeeResponse;
import org.mss301.employeeservice.dto.response.UserResponse;
import org.mss301.employeeservice.entity.Employee;
import org.mss301.employeeservice.entity.enumeration.EmployeeStatus;
import org.mss301.employeeservice.mapper.EmployeeMapper;
import org.mss301.employeeservice.repository.EmployeeRepository;
import org.mss301.employeeservice.service.EmployeeService;
import org.mss301.employeeservice.specification.EmployeeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final IdentityServiceClient identityServiceClient;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Long shopId = requireShopId();

        if (request.getUserId() == null) {
            throw new BusinessException("userId không được để trống");
        }

        if (employeeRepository.existsByShopIdAndUserId(shopId, request.getUserId())) {
            throw new BusinessException("User này đã là nhân viên của cửa hàng");
        }
        Employee employee = employeeMapper.toEntity(request, shopId);
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Long shopId = requireShopId();
        Employee employee = findEmployeeByIdAndShop(id, shopId);

        EmployeeResponse response = employeeMapper.toResponse(employee);
        enrichWithUserProfile(response);
        return response;
    }

    @Override
    public PageResponse<EmployeeResponse> getAllEmployees(EmployeeFilter filter) {
        Long shopId = requireShopId();
        Specification<Employee> spec = EmployeeSpecification.filter(shopId, filter)
                .and((root, query, cb) -> cb.notEqual(root.get("status"), EmployeeStatus.DELETED));
        Page<Employee> page = employeeRepository.findAll(spec, filter.toPageable());
        Page<EmployeeResponse> responses = page.map(employeeMapper::toResponse);
        responses.forEach(this::enrichWithUserProfile);
        return PageResponse.of(responses);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Long shopId = requireShopId();
        Employee employee = findActiveEmployeeByIdAndShop(id, shopId);
        employeeMapper.updateEntity(request, employee);
        EmployeeResponse response = employeeMapper.toResponse(employeeRepository.save(employee));
        enrichWithUserProfile(response);
        return response;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Long shopId = requireShopId();
        Employee employee = findActiveEmployeeByIdAndShop(id, shopId);
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
    }

    private Employee findEmployeeByIdAndShop(Long id, Long shopId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));

        if (!employee.getShopId().equals(shopId)) {
            throw new BusinessException("Bạn không có quyền truy cập nhân viên này");
        }

        return employee;
    }

    private Employee findActiveEmployeeByIdAndShop(Long id, Long shopId) {
        Employee employee = findEmployeeByIdAndShop(id, shopId);

        if (EmployeeStatus.DELETED.equals(employee.getStatus())) {
            throw new BusinessException("Nhân viên này đã bị xóa");
        }

        return employee;
    }

    private void enrichWithUserProfile(EmployeeResponse response) {
        if (response.getUserId() != null) {
            try {
                String domain = getCurrentDomain();
                UserResponse user = identityServiceClient.getUserById(response.getUserId(), domain).getBody();
                response.setUser(user);
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