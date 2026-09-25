package com.shivansh.cakes.auth.service.impl;

import com.shivansh.cakes.admin.entity.Admin;
import com.shivansh.cakes.admin.repository.AdminRepository;
import com.shivansh.cakes.auth.dto.request.*;
import com.shivansh.cakes.auth.dto.response.AdminAuthResponse;
import com.shivansh.cakes.auth.dto.response.AuthResponse;
import com.shivansh.cakes.auth.entity.EmailVerification;
import com.shivansh.cakes.auth.entity.PasswordResetOtp;
import com.shivansh.cakes.auth.repository.EmailVerificationRepository;
import com.shivansh.cakes.auth.repository.PasswordResetOtpRepository;
import com.shivansh.cakes.auth.service.AuthService;
import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.email.EmailService;
import com.shivansh.cakes.security.JwtService;
import com.shivansh.cakes.user.entity.User;
import com.shivansh.cakes.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implements all auth flows described in BRS §6.2.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final String baseUrl;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(
            UserRepository userRepository,
            AdminRepository adminRepository,
            EmailVerificationRepository emailVerificationRepository,
            PasswordResetOtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.baseUrl = baseUrl;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6.2.1 Registration
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }
        if (userRepository.existsByContactNumber(request.contactNumber())) {
            throw new BusinessException("Contact number already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setContactNumber(request.contactNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setIsVerified(false);
        user.setStatus(false); // not blocked
        User saved = userRepository.save(user);

        sendVerificationEmail(saved);

        return ApiResponse.success("Verification email sent. Please check your inbox.");
    }

    private void sendVerificationEmail(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken);

        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .uniqueString(hashedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(2))
                .build();
        emailVerificationRepository.save(verification);

        String verifyUrl = "%s/api/auth/verify?userId=%d&token=%s"
                .formatted(baseUrl, user.getId(), rawToken);
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), verifyUrl);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6.2.2 Email Verification
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> verifyEmail(Long userId, String token) {
        EmailVerification verification = emailVerificationRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException("Verification record not found"));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Delete verification + delete unverified user
            emailVerificationRepository.deleteAllByUserId(userId);
            userRepository.deleteById(userId);
            throw new BusinessException("Verification link expired. Please register again.");
        }

        if (!passwordEncoder.matches(token, verification.getUniqueString())) {
            throw new BusinessException("Invalid verification link");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsVerified(true);
        userRepository.save(user);

        emailVerificationRepository.deleteAllByUserId(userId);
        return ApiResponse.success("Email verified successfully.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6.2.3 User Login
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with that email"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Wrong password");
        }
        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new BusinessException("Email not verified. Check your inbox.");
        }
        if (Boolean.TRUE.equals(user.getStatus())) {
            throw new BusinessException("Account is blocked. Contact support.");
        }

        String token = jwtService.generateAccessToken(user.getId(), user.getEmail(), "ROLE_USER");
        return AuthResponse.of(token, user.getId(), user.getName(), user.getEmail(), user.getImage());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6.2.4 Admin Login
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public AdminAuthResponse adminLogin(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new BusinessException("Invalid credentials");
        }

        String token = jwtService.generateAccessToken(admin.getId(), admin.getEmail(), "ROLE_ADMIN");
        return AdminAuthResponse.of(token);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6.2.5 Forgot Password / OTP
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ApiResponse<Long> checkEmail(CheckEmailRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with that email"));

        String otp = generateOtp();
        otpRepository.deleteAllByUserId(user.getId());
        otpRepository.save(PasswordResetOtp.builder()
                .user(user)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .build());

        emailService.sendOtpEmail(user.getEmail(), otp);
        return ApiResponse.success("OTP sent to your email.", user.getId());
    }

    @Override
    public ApiResponse<Void> verifyOtp(Long userId, VerifyOtpRequest request) {
        PasswordResetOtp otp = otpRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException("OTP not found or already used"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.deleteAllByUserId(userId);
            throw new BusinessException("OTP expired. Please request a new one.");
        }
        if (!otp.getOtp().equals(request.otp())) {
            throw new BusinessException("Incorrect OTP");
        }

        otpRepository.deleteAllByUserId(userId);
        return ApiResponse.success("OTP verified.");
    }

    @Override
    public ApiResponse<Void> changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return ApiResponse.success("Password changed successfully.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6.2.6 Resend OTP
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ApiResponse<Void> resendOtp(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String otp = generateOtp();
        otpRepository.deleteAllByUserId(userId);
        otpRepository.save(PasswordResetOtp.builder()
                .user(user)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .build());

        emailService.sendOtpEmail(user.getEmail(), otp);
        return ApiResponse.success("OTP resent.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Generates a 6-digit numeric OTP using SecureRandom (BRS §8.3). */
    private String generateOtp() {
        int otpInt = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(otpInt);
    }
}
