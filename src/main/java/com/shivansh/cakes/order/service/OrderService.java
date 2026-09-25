package com.shivansh.cakes.order.service;

import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.order.dto.request.OrderRequest;
import com.shivansh.cakes.order.dto.response.OrderResponse;
import com.shivansh.cakes.order.entity.type.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrderService {
    // User operations
    OrderResponse createOrder(String email, OrderRequest request);
    List<OrderResponse> getUserOrders(String email);
    OrderResponse getOrder(String email, Long id);
    OrderResponse cancelOrder(String email, Long id);

    // Payment Integration (Razorpay)
    OrderResponse verifyPayment(String email, Long id, String paymentId, String signature);

    // Admin operations
    PageResponse<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
}
