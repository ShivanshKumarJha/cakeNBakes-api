package com.shivansh.cakes.auth.repository;

import com.shivansh.cakes.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    List<EmailVerification> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
