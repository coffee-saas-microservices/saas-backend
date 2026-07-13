package org.mss301.identityservice.controller;

import lombok.RequiredArgsConstructor;
import org.mss301.identityservice.dto.response.UserResponse;
import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.repository.UserRepository;
import org.mss301.identityservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/by-keycloak/{keycloakUserId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(@PathVariable("keycloakUserId") String keycloakUserId) {
        return ResponseEntity.ok(userService.getUserProfile(keycloakUserId));
    }
    @GetMapping("/{id}/point-rate")
    public ResponseEntity<Float> getUserPointRate(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(User::getMembershipRank)
                .map(rank -> rank != null ? rank.getPointRate() : null)
                .map(rate -> ResponseEntity.ok(rate != null ? rate : 1.0f))
                .orElse(ResponseEntity.ok(1.0f)); 
    }
}


