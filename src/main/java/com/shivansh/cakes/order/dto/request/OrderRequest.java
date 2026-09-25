package com.shivansh.cakes.order.dto.request;

public record OrderRequest(
        String couponCode,
        Long addressId
) {}
