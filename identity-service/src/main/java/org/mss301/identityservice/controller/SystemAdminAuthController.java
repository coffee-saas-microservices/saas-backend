package org.mss301.identityservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mss301.identityservice.dto.request.LoginRequest;
import org.mss301.identityservice.dto.request.SystemAdminRegistrationRequest;
import org.mss301.identityservice.dto.response.LoginResponse;
import org.mss301.identityservice.dto.response.SystemAdminRegistrationResponse;
import org.mss301.identityservice.service.SystemAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-admin")
@RequiredArgsConstructor
public class SystemAdminAuthController {

    private final SystemAuthService systemAuthService;

    @PostMapping("/register")
    public ResponseEntity<SystemAdminRegistrationResponse> register(
            @Valid @RequestBody SystemAdminRegistrationRequest request
            ) {
        SystemAdminRegistrationResponse response = systemAuthService.registerSystemAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
            ) {
        LoginResponse response = systemAuthService.loginSystemAdmin(request);
        return ResponseEntity.ok(response);
    }
}
