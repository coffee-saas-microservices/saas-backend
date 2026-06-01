package org.mss301.identityservice.repository;

import org.mss301.identityservice.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findFirstByEmailOrderByExpiryDateDesc(String email);
    void deleteByEmail(String email);
}
