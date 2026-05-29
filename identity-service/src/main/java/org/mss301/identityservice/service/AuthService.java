package org.mss301.identityservice.service;

import org.mss301.identityservice.dto.request.CustomerRegistrationRequest;
import org.mss301.identityservice.dto.request.LoginRequest;
import org.mss301.identityservice.dto.request.LogoutRequest;
import org.mss301.identityservice.dto.response.CustomerResponse;
import org.mss301.identityservice.dto.response.LoginResponse;

public interface AuthService {
    CustomerResponse register(CustomerRegistrationRequest request);
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request);
}
