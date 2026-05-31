package org.mss301.identityservice.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mss301.commonservice.exception.BusinessException;
import org.mss301.commonservice.keycloak.KeyCloakAuthClient;
import org.mss301.commonservice.keycloak.KeyCloakTokenResponse;
import org.mss301.commonservice.multitenancy.TenantContext;
import org.mss301.identityservice.dto.request.*;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.entity.PasswordResetToken;
import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.entity.enumeration.UserStatus;
import org.mss301.identityservice.mapper.UserMapper;
import org.mss301.identityservice.repository.PasswordResetTokenRepository;
import org.mss301.identityservice.repository.UserRepository;
import org.mss301.identityservice.repository.MembershipRankRepository;
import org.mss301.identityservice.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

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
            try {
                keyCloakAuthClient.deleteUser(keycloakUserId);
            } catch (Exception e) {
                log.error("Không thể rollback Keycloak user: {}", keycloakUserId, e);
            }
            throw ex;
        }
    }

    @Override
    public void changePassword(String keycloakUserId, ChangePasswordRequest request) {
        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new BusinessException("Người dùng không tồn tại"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("Tài khoản đã bị khóa");
        }

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không đúng");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        keyCloakAuthClient.updateUserPassword(keycloakUserId, request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Long shopId = TenantContext.getCurrentShopId();
        if (shopId == null) throw new BusinessException("Cửa hàng không tồn tại");

        User user = userRepository.findAll()
                .stream()
                .filter(u -> u.getEmail() != null
                        && u.getEmail().equalsIgnoreCase(request.getEmail().trim())
                        && u.getShopId().equals(shopId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Không tìm thấy email"));

        tokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("[COFFEE SAAS] YÊU CẦU ĐẶT LẠI MẬT KHẨU MỚI");
            String content = "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                    + "<h3>Xin chào " + (user.getFullname() != null ? user.getFullname() : user.getUsername()) + ",</h3>"
                    + "<p>Hệ thống nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với Email này của bạn.</p>"
                    + "<p>Vui lòng bấm vào liên kết dưới đây để thực hiện thay đổi mật khẩu (Liên kết có giá trị trong vòng 15 phút):</p>"
                    + "<p style='margin: 20px 0;'><a href=\"" + resetUrl + "\" style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Đặt lại mật khẩu mới tại đây</a></p>"
                    + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>"
                    + "</div>";
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException("Không thể gửi email lúc này. Vui lòng thử lại sau!");
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.isPasswordMatch()) {
            throw new BusinessException("Mật khẩu không trùng khớp");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Liên kết đổi mật khẩu không hợp lệ hoặc đã hết hạn"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BusinessException("Liên kết đổi mật khẩu đã hết hạn, vui lòng yêu cầu gửi lại email mới");
        }

        User user = resetToken.getUser();
        keyCloakAuthClient.resetUserPassword(user.getKeycloakUserId(), request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
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
