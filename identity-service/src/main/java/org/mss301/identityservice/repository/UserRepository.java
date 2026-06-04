package org.mss301.identityservice.repository;

import org.mss301.identityservice.entity.User;
import org.mss301.identityservice.entity.enumeration.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    boolean existsByUsernameAndShopId(String username, Long shopId);

    boolean existsByEmailAndShopId(String email, Long shopId);

    boolean existsByPhoneAndShopId(String phone, Long shopId);

    Optional<User> findByEmailIgnoreCaseAndShopId(String email, Long shopId);

    Optional<User> findByIdAndShopIdAndStatusNot(Long id, Long shopId, UserStatus status);
}
