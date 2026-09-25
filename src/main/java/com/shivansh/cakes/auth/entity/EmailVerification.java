package com.shivansh.cakes.auth.entity;

import com.shivansh.cakes.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores the BCrypt-hashed UUID token sent in the email verification link.
 * Mirrors the source's userEmailverificationModel.js.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** BCrypt hash of the raw UUID token sent in the verification link. */
    @Column(nullable = false)
    private String uniqueString;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 2 hours after createdAt per BRS §8.2. */
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
