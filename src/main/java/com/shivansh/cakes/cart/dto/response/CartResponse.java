package com.shivansh.cakes.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> cartItems,
        BigDecimal cartTotal,
        Integer itemCount
) {}
