package com.shivansh.cakes.cart.dto.response;

import com.shivansh.cakes.product.dto.response.ProductResponse;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Integer quantity,
        Double weight,
        BigDecimal price,
        BigDecimal subTotal,
        ProductResponse product
) {}
