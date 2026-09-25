package com.shivansh.cakes.coupon.dto.response;

import java.time.LocalDate;

public record CouponResponse(
        Long id,
        String couponCode,
        Double discount,
        String description,
        Double minimum,
        LocalDate expiryDate
) {}
