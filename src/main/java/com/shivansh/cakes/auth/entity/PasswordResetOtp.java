package com.shivansh.cakes.auth.entity;

import com.shivansh.cakes.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores the 6-digit OTP for password reset.
 * Mirrors the source's userPasswordResetModel.js.
 * OTP expires in 3 minutes per BRS §8.3.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset_otp")
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 3 minutes after createdAt per BRS §8.3. */
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
