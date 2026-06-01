package org.mss301.identityservice.repository;

import org.mss301.identityservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    boolean existsByUsernameAndShopId(String username, Long shopId);

    boolean existsByEmailAndShopId(String email, Long shopId);

    boolean existsByPhoneAndShopId(String phone, Long shopId);

    Optional<User> findByEmailIgnoreCaseAndShopId(String email, Long shopId);
}
