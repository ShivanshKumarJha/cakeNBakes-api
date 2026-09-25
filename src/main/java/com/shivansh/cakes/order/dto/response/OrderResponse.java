package com.shivansh.cakes.order.dto.response;

import com.shivansh.cakes.order.entity.type.OrderStatus;
import com.shivansh.cakes.product.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        BigDecimal orderTotal,
        BigDecimal shippingCost,
        BigDecimal discount,
        OrderStatus status,
        LocalDateTime deliveryDate,
        String deliveryAddress,
        String razorpayOrderId,
        String razorpayPaymentId,
        List<OrderItemResponse> items
) {}
