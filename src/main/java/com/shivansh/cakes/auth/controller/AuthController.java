package com.shivansh.cakes.auth.controller;

import com.shivansh.cakes.auth.dto.request.*;
import com.shivansh.cakes.auth.dto.response.AdminAuthResponse;
import com.shivansh.cakes.auth.dto.response.AuthResponse;
import com.shivansh.cakes.auth.service.AuthService;
import com.shivansh.cakes.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth endpoints — all public per BRS §6.3.
 * Maps to: POST /api/auth/*, GET /api/auth/verify
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login, email verification, OTP password reset")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user — sends verification email")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify email via link (userId + token query params)")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @RequestParam Long userId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(authService.verifyEmail(userId, token));
    }

    @PostMapping("/login")
    @Operation(summary = "User login — returns JWT access token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Admin login — returns JWT access token")
    public ResponseEntity<AdminAuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout — stateless; client discards token")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }

    @PostMapping("/admin/logout")
    @Operation(summary = "Admin logout — stateless; client discards token")
    public ResponseEntity<ApiResponse<Void>> adminLogout() {
        return ResponseEntity.ok(ApiResponse.success("Admin logged out successfully."));
    }

    @PostMapping("/check-email")
    @Operation(summary = "Send password-reset OTP to email")
    public ResponseEntity<ApiResponse<Long>> checkEmail(@Valid @RequestBody CheckEmailRequest request) {
        return ResponseEntity.ok(authService.checkEmail(request));
    }

    @PostMapping("/verify-otp/{userId}")
    @Operation(summary = "Verify the password-reset OTP")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @PathVariable Long userId,
            @Valid @RequestBody VerifyOtpRequest request
    ) {
        return ResponseEntity.ok(authService.verifyOtp(userId, request));
    }

    @PostMapping("/change-password/{userId}")
    @Operation(summary = "Set new password after OTP verification")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(authService.changePassword(userId, request));
    }

    @PostMapping("/resend-otp/{userId}")
    @Operation(summary = "Resend password-reset OTP")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.resendOtp(userId));
    }
}
