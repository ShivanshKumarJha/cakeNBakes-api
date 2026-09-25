package com.shivansh.cakes.coupon.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CouponRequest(
        @NotBlank(message = "Coupon code is required")
        @Size(max = 50, message = "Code must not exceed 50 characters")
        String couponCode,

        @NotNull(message = "Discount is required")
        @DecimalMin(value = "0.01", message = "Discount must be positive")
        Double discount,

        @Size(max = 100, message = "Description must not exceed 100 characters")
        String description,

        Double minimum,

        LocalDate expiryDate
) {}
