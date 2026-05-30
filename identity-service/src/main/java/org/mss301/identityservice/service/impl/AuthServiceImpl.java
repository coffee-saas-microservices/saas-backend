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
import org.mss301.identityservice.dto.request.ShopAccountRequest;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.entity.Customer;
import org.mss301.identityservice.entity.UserProfile;
import org.mss301.identityservice.entity.enumeration.CustomerStatus;
import org.mss301.identityservice.mapper.CustomerMapper;
import org.mss301.identityservice.repository.CustomerRepository;
import org.mss301.identityservice.repository.MembershipRankRepository;
import org.mss301.identityservice.repository.UserProfileRepository;
import org.mss301.identityservice.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository customerRepository;
    private final KeyCloakAuthClient keyCloakAuthClient;
    private final CustomerMapper customerMapper;
    private final MembershipRankRepository membershipRankRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

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

        KeyCloakTokenResponse tokenResponse;
        try {
            tokenResponse = keyCloakAuthClient.login(request.getUsername(), request.getPassword());
        } catch (Exception e) {
            throw new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        Map<String, Object> payload = decodeJwtPayload(tokenResponse.getAccessToken());
        String keycloakUserId = (String) payload.get("sub");
        
        Map<String, Object> realmAccess = (Map<String, Object>) payload.get("realm_access");
        List<String> roles = realmAccess != null ? (List<String>) realmAccess.get("roles") : List.of();

        if (roles.contains("CUSTOMER")) {
            Customer customer = customerRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác"));

            if (customer.getShopId() == null || !customer.getShopId().equals(currentShopId)) {
                throw new BusinessException("Tài khoản không thuộc cửa hàng này");
            }

            if (customer.getStatus() != CustomerStatus.ACTIVE) {
                throw new BusinessException("Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động");
            }
        } else if (roles.contains("SHOP_ADMIN")) {
            UserProfile userProfile = userProfileRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác"));

            if (userProfile.getShopId() == null || !userProfile.getShopId().equals(currentShopId)) {
                throw new BusinessException("Tài khoản không thuộc cửa hàng này");
            }
        } else {
            throw new BusinessException("Tài khoản không có quyền truy cập cửa hàng này");
        }

        return LoginResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        keyCloakAuthClient.logout(request.getRefreshToken());
    }

    @Override
    @Transactional
    public void registerShopAccount(ShopAccountRequest request) {
        Map<String, List<String>> extraAttributes = new HashMap<>();
        extraAttributes.put("shopId", List.of(request.getShopId().toString()));

        String keycloakUserId = keyCloakAuthClient.createUserWithAttributes(
                request.getUsername(),
                request.getEmail(),
                (request.getFullName() != null && !request.getFullName().isBlank()) ? request.getFullName() : request.getUsername(),
                request.getPhone(),
                request.getPassword(),
                List.of("SHOP_ADMIN"),
                extraAttributes
        );

        try {
            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(request.getUsername());
            userProfile.setEmail(request.getEmail());
            userProfile.setFullname(request.getFullName());
            userProfile.setPhone(request.getPhone());
            userProfile.setShopId(request.getShopId());
            userProfile.setKeycloakUserId(keycloakUserId);
            userProfileRepository.save(userProfile);
            log.info("Đã tạo shop account trong DB (user_profiles) cho user: {}", request.getUsername());
        } catch (Exception ex) {
            // Rollback Keycloak user nếu lưu DB thất bại
            try {
                keyCloakAuthClient.deleteUser(keycloakUserId);
                log.info("Đã rollback Keycloak user: {}", keycloakUserId);
            } catch (Exception e) {
                log.error("Không thể rollback Keycloak user: {}", keycloakUserId, e);
            }
            throw ex;
        }
    }

    private Map<String, Object> decodeJwtPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new BusinessException("Token Keycloak không hợp lệ");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return new ObjectMapper().readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Lỗi giải mã access token: {}", e.getMessage(), e);
            throw new BusinessException("Xác thực token thất bại");
        }
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
