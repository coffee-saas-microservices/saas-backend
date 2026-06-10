package org.mss301.employeeservice.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.dto.response.PageResponse;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.employeeservice.client.IdentityServiceClient;
import org.mss301.employeeservice.dto.request.ScheduleFilter;
import org.mss301.employeeservice.dto.request.ScheduleRequest;
import org.mss301.employeeservice.dto.response.ScheduleResponse;
import org.mss301.employeeservice.dto.response.UserResponse;
import org.mss301.employeeservice.entity.Employee;
import org.mss301.employeeservice.entity.Schedule;
import org.mss301.employeeservice.entity.enumeration.ScheduleStatus;
import org.mss301.employeeservice.mapper.ScheduleMapper;
import org.mss301.employeeservice.repository.EmployeeRepository;
import org.mss301.employeeservice.repository.ScheduleRepository;
import org.mss301.employeeservice.service.ScheduleService;
import org.mss301.employeeservice.specification.ScheduleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleMapper mapper;
    private final IdentityServiceClient identityServiceClient;

    @Override
    @Transactional
    public ScheduleResponse create(ScheduleRequest request) {
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

        Schedule entity = mapper.toEntity(request);
        entity.setEmployee(employee);
        entity.setShopId(shopId);

        if (request.getStartTime() != null) {
            entity.setDayOfWeek(request.getStartTime().getDayOfWeek());
        }

        Schedule saved = scheduleRepository.save(entity);
        ScheduleResponse response = mapper.toResponse(saved);
        enrichWithEmployeeName(response, employee.getUserId());
        return response;
    }

    @Override
    @Transactional
    public ScheduleResponse update(Long id, ScheduleRequest request) {
        Long shopId = requireShopId();

        Schedule entity = scheduleRepository.findByScheduleIdAndShopId(id, shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lịch làm việc"));

        if (ScheduleStatus.DELETED.equals(entity.getStatus())) {
            throw new BusinessException("Lịch làm việc này đã bị xóa");
        }

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
        }

        Schedule saved = scheduleRepository.save(entity);
        ScheduleResponse response = mapper.toResponse(saved);
        enrichWithEmployeeName(response, entity.getEmployee().getUserId());
        return response;
    }

    @Override
    public ScheduleResponse getDetail(Long id) {
        Long shopId = requireShopId();

        Schedule entity = scheduleRepository.findByScheduleIdAndShopId(id, shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lịch làm việc"));

        ScheduleResponse response = mapper.toResponse(entity);
        enrichWithEmployeeName(response, entity.getEmployee().getUserId());
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long shopId = requireShopId();

        Schedule entity = scheduleRepository.findByScheduleIdAndShopId(id, shopId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lịch làm việc"));

        entity.setStatus(ScheduleStatus.DELETED);
        scheduleRepository.save(entity);
    }

    @Override
    public PageResponse<ScheduleResponse> getAll(ScheduleFilter filter) {
        Long shopId = requireShopId();
        Specification<Schedule> spec = ScheduleSpecification.filter(shopId, filter);

        Page<Schedule> page = scheduleRepository.findAll(spec, filter.toPageable());

        Page<ScheduleResponse> responsePage = page.map(entity -> {
            ScheduleResponse res = mapper.toResponse(entity);
            if (entity.getEmployee() != null) {
                enrichWithEmployeeName(res, entity.getEmployee().getUserId());
            }
            return res;
        });

        return PageResponse.of(responsePage);
    }

    @Override
    public List<ScheduleResponse> getByEmployeeId(Long employeeId) {
        Long shopId = requireShopId();

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));

        if (!employee.getShopId().equals(shopId)) {
            throw new BusinessException("Nhân viên không thuộc cửa hàng này");
        }

        List<Schedule> schedules = scheduleRepository.findAllByEmployeeEmployeeIdAndShopId(employeeId, shopId);

        return schedules.stream().map(entity -> {
            ScheduleResponse res = mapper.toResponse(entity);
            enrichWithEmployeeName(res, employee.getUserId());
            return res;
        }).collect(Collectors.toList());
    }

    private void enrichWithEmployeeName(ScheduleResponse response, Long userId) {
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
