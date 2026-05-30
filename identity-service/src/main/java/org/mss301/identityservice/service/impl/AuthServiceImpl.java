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
import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.entity.enumeration.UserStatus;
import org.mss301.identityservice.mapper.UserMapper;
import org.mss301.identityservice.repository.UserRepository;
import org.mss301.identityservice.repository.MembershipRankRepository;
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
    private final UserRepository userRepository;
    private final KeyCloakAuthClient keyCloakAuthClient;
    private final UserMapper userMapper;
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
            User user = buildCustomer(request, shopId, keycloakUserId);
            user = userRepository.save(user);

            user.setMembershipRank(membershipRankRepository
                    .findFirstByShopIdOrderByRequiredPointsAsc(shopId)
                    .orElseThrow(() -> new BusinessException("Lỗi cấu hình hạng thành viên!")));
            user = userRepository.save(user);

            return userMapper.toResponse(user);
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
            User user = userRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác"));

            if (user.getShopId() == null || !user.getShopId().equals(currentShopId)) {
                throw new BusinessException("Tài khoản không thuộc cửa hàng này");
            }

            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new BusinessException("Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động");
            }
        } else if (roles.contains("SHOP_ADMIN")) {
            User user = userRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không chính xác"));

            if (user.getShopId() == null || !user.getShopId().equals(currentShopId)) {
                throw new BusinessException("Tài khoản không thuộc cửa hàng này");
            }

            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new BusinessException("Tài khoản admin của bạn đã bị khóa hoặc ngừng hoạt động");
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
            User shopAdmin = new User();
            shopAdmin.setUsername(request.getUsername());
            shopAdmin.setEmail(request.getEmail());
            shopAdmin.setFullname(request.getFullName());
            shopAdmin.setPhone(request.getPhone());
            shopAdmin.setShopId(request.getShopId());
            shopAdmin.setKeycloakUserId(keycloakUserId);
            shopAdmin.setStatus(UserStatus.ACTIVE);
            shopAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(shopAdmin);
        } catch (Exception ex) {
            // Rollback Keycloak user nếu lưu DB thất bại
            try {
                keyCloakAuthClient.deleteUser(keycloakUserId);
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
        if (userRepository.existsByUsername(request.getUsername()))
            throw new BusinessException("Tên đăng nhập đã tồn tại");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BusinessException("Email đã tồn tại");

        if (request.getPhone() != null && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone()))
            throw new BusinessException("Số điện thoại đã được sử dụng");
    }

    private User buildCustomer(CustomerRegistrationRequest request, Long shopId, String keycloakUserId) {
        User user = userMapper.toEntity(request);
        user.setShopId(shopId);
        user.setTotalPoint(0.0);
        user.setKeycloakUserId(keycloakUserId);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return user;
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
