package com.shivansh.cakes.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResendOtpRequest(
        @NotBlank(message = "userId is required")
        Long userId
) {}
