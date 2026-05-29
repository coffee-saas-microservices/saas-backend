package org.mss301.identityservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.keycloak.KeyCloakAuthClient;
import org.mss301.commonservice.keycloak.KeyCloakTokenResponse;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.identityservice.dto.request.CustomerRegistrationRequest;
import org.mss301.identityservice.dto.request.LoginRequest;
import org.mss301.identityservice.dto.request.LogoutRequest;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.entity.Customer;
import org.mss301.identityservice.entity.enumeration.CustomerStatus;
import org.mss301.identityservice.mapper.CustomerMapper;
import org.mss301.identityservice.repository.CustomerRepository;
import org.mss301.identityservice.repository.MembershipRankRepository;
import org.mss301.identityservice.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository customerRepository;
    private final KeyCloakAuthClient keyCloakAuthClient;
    private final CustomerMapper customerMapper;
    private final MembershipRankRepository membershipRankRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CustomerResponse register(CustomerRegistrationRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null)
            throw new BusinessException("Cửa hàng không tồn tại");

        validateUniqueness(request);

        String dobStr = request.getDob() != null ? request.getDob().toString() : null;
        String createdStr = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String keycloakUserId = keyCloakAuthClient.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getFullname(),
                request.getPhone(),
                request.getAddress(),
                dobStr,
                createdStr,
                request.getPassword(),
                List.of("CUSTOMER"));

        try {
            Customer customer = buildCustomer(request, shopId, keycloakUserId);
            customer = customerRepository.save(customer);

            customer.setMembershipRank(membershipRankRepository
                    .findFirstByShopIdOrderByRequiredPointsAsc(shopId)
                    .orElseThrow(() -> new BusinessException("Lỗi cấu hình hạng thành viên!")));
            customer = customerRepository.save(customer);

            return customerMapper.toResponse(customer);
        } catch (Exception ex) {
            rollbackKeycloakUser(keycloakUserId);
            throw ex;
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException("Thiếu thông tin đăng nhập");
        }

        Long currentShopId = TenantContext.getCurrentShopId();
        if (currentShopId == null) {
            throw new BusinessException("Cửa hàng không tồn tại");
        }

        Customer customer = customerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        if (customer.getShopId() == null || !customer.getShopId().equals(currentShopId)) {
            throw new BusinessException("Tài khoản không thuộc cửa hàng này");
        }

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new BusinessException("Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động");
        }

        return generateLoginResponse(request);
    }

    @Override
    public void logout(LogoutRequest request) {
        keyCloakAuthClient.logout(request.getRefreshToken());
    }

    private LoginResponse generateLoginResponse(LoginRequest request) {
        KeyCloakTokenResponse tokenResponse = keyCloakAuthClient.login(request.getUsername(), request.getPassword());

        return LoginResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                .build();
    }

    private void validateUniqueness(CustomerRegistrationRequest request) {
        if (customerRepository.existsByUsername(request.getUsername()))
            throw new BusinessException("Tên đăng nhập đã tồn tại");

        if (customerRepository.existsByEmail(request.getEmail()))
            throw new BusinessException("Email đã tồn tại");

        if (request.getPhone() != null && !request.getPhone().isBlank()
                && customerRepository.existsByPhone(request.getPhone()))
            throw new BusinessException("Số điện thoại đã được sử dụng");
    }

    private Customer buildCustomer(CustomerRegistrationRequest request, Long shopId, String keycloakUserId) {
        Customer customer = customerMapper.toEntity(request);
        customer.setShopId(shopId);
        customer.setTotalPoint(0.0);
        customer.setKeycloakUserId(keycloakUserId);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        return customer;
    }

    private void rollbackKeycloakUser(String keycloakUserId) {
        try {
            keyCloakAuthClient.deleteUser(keycloakUserId);
            log.info("Đã rollback Keycloak user: {}", keycloakUserId);
        } catch (Exception e) {
            log.error("Không thể rollback Keycloak user: {}", keycloakUserId, e);
        }
    }
}
