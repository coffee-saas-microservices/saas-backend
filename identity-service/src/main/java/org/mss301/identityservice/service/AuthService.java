package org.mss301.identityservice.service;

import org.mss301.identityservice.dto.request.*;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.dto.response.SystemAdminRegistrationResponse;

public interface AuthService {
    CustomerResponse register(CustomerRegistrationRequest request);
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request);
    void registerShopAccount(ShopAccountRequest request);
    void changePassword(String keycloakUserId, ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
