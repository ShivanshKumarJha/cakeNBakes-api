package com.shivansh.cakes.auth.service;

import com.shivansh.cakes.auth.dto.request.*;
import com.shivansh.cakes.auth.dto.response.AdminAuthResponse;
import com.shivansh.cakes.auth.dto.response.AuthResponse;
import com.shivansh.cakes.common.response.ApiResponse;

public interface AuthService {

    ApiResponse<Void> register(RegisterRequest request);

    ApiResponse<Void> verifyEmail(Long userId, String token);

    AuthResponse login(LoginRequest request);

    AdminAuthResponse adminLogin(LoginRequest request);

    ApiResponse<Long> checkEmail(CheckEmailRequest request);

    ApiResponse<Void> verifyOtp(Long userId, VerifyOtpRequest request);

    ApiResponse<Void> changePassword(Long userId, ChangePasswordRequest request);

    ApiResponse<Void> resendOtp(Long userId);
}
