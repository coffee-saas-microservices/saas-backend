package org.mss301.identityservice.service;

import org.mss301.identityservice.dto.request.LoginRequest;
import org.mss301.identityservice.dto.request.SystemAdminRegistrationRequest;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.dto.response.SystemAdminRegistrationResponse;

public interface SystemAuthService {
    SystemAdminRegistrationResponse registerSystemAdmin(SystemAdminRegistrationRequest request);
    LoginResponse loginSystemAdmin(LoginRequest request);
}
