package org.mss301.inventoryservice.client;

import org.mss301.inventoryservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

    @GetMapping("/api/internal/users/by-keycloak/{keycloakUserId}")
    ResponseEntity<UserResponse> getUserByKeycloakId(
            @PathVariable("keycloakUserId") String keycloakUserId,
            @RequestHeader("X-Forwarded-Host") String domain);

    @GetMapping("/api/internal/users/{id}")
    ResponseEntity<UserResponse> getUserById(
            @PathVariable("id") Long id,
            @RequestHeader("X-Forwarded-Host") String domain);
}
