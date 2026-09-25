package com.shivansh.cakes.order.dto.response;

import com.shivansh.cakes.product.dto.response.ProductResponse;
import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Integer quantity,
        BigDecimal price,
        ProductResponse product
) {}
