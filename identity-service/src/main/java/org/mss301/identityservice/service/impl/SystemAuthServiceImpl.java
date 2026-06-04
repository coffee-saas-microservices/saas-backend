package org.mss301.identityservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.keycloak.KeyCloakAuthClient;
import org.mss301.commonservice.keycloak.KeyCloakTokenResponse;
import org.mss301.identityservice.dto.request.LoginRequest;
import org.mss301.identityservice.dto.request.SystemAdminRegistrationRequest;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.dto.response.SystemAdminRegistrationResponse;
import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.entity.enumeration.UserStatus;
import org.mss301.identityservice.entity.enumeration.UserType;
import org.mss301.identityservice.mapper.SystemAdminMapper;
import org.mss301.identityservice.repository.UserRepository;
import org.mss301.identityservice.service.SystemAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAuthServiceImpl implements SystemAuthService {
    private final UserRepository userRepository;
    private final KeyCloakAuthClient keyCloakAuthClient;
    private final PasswordEncoder passwordEncoder;
    private final SystemAdminMapper systemAdminMapper;

    @Override
    public SystemAdminRegistrationResponse registerSystemAdmin(SystemAdminRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Tên đăng nhập quản trị viên đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email quản trị viên đã tồn tại");
        }

        Map<String, List<String>> extraAttributes = new HashMap<>();
        extraAttributes.put("userType", List.of("SYSTEM_ADMIN"));

        String keycloakUserId = keyCloakAuthClient.createUserWithAttributes(
                request.getUsername(),
                request.getEmail(),
                request.getFullName(),
                request.getPhone(),
                request.getPassword(),
                List.of("SYSTEM_ADMIN"),
                extraAttributes
        );

        try {
            User systemAdmin = systemAdminMapper.toEntity(request);

            systemAdmin.setShopId(0L);
            systemAdmin.setKeycloakUserId(keycloakUserId);
            systemAdmin.setStatus(UserStatus.ACTIVE);
            systemAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
            systemAdmin.setTotalPoint(0.0);
            systemAdmin.setUserType(UserType.SYSTEM_ADMIN);
            User savedAdmin = userRepository.save(systemAdmin);

            return systemAdminMapper.toResponse(savedAdmin);
        } catch (Exception ex) {
            try {
                keyCloakAuthClient.deleteUser(keycloakUserId);
            } catch (Exception e) {
                log.error("Không thể rollback Keycloak user: {}", keycloakUserId, e);
            }
            throw ex;
        }
    }

    @Override
    public LoginResponse loginSystemAdmin(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException("Thiếu thông tin đăng nhập");
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

        if (!roles.contains("SYSTEM_ADMIN")) {
            throw new BusinessException("Tài khoản không có quyền truy cập hệ thống");
        }

        User admin = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new BusinessException("Tài khoản quản trị viên không tồn tại"));

        if (admin.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("Tài khoản quản trị viên đã bị khóa hoặc ngừng hoạt động");
        }

        return LoginResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                .build();
    }

    private Map<String, Object> decodeJwtPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new BusinessException("Token Keycloak không hợp lệ");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return new ObjectMapper().readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Xác thực token thất bại");
        }
    }
}
