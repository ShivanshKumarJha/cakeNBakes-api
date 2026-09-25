package com.shivansh.cakes.order.controller;

import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.order.dto.request.OrderRequest;
import com.shivansh.cakes.order.dto.response.OrderResponse;
import com.shivansh.cakes.order.entity.type.OrderStatus;
import com.shivansh.cakes.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Orders", description = "User order management and payment integration")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/orders")
    @Operation(summary = "Create an order from cart (Generates Razorpay Order)")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Principal principal,
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created.", orderService.createOrder(principal.getName(), request)));
    }

    @PostMapping("/api/orders/{id}/verify-payment")
    @Operation(summary = "Verify Razorpay payment signature")
    public ResponseEntity<ApiResponse<OrderResponse>> verifyPayment(
            Principal principal,
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(ApiResponse.success("Payment verified.",
                orderService.verifyPayment(
                        principal.getName(),
                        id,
                        body.get("razorpayPaymentId"),
                        body.get("razorpaySignature")
                )));
    }

    @GetMapping("/api/orders")
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(principal.getName())));
    }

    @GetMapping("/api/orders/{id}")
    @Operation(summary = "Get order details")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(principal.getName(), id)));
    }

    @PatchMapping("/api/orders/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled.", orderService.cancelOrder(principal.getName(), id)));
    }

    // ── Admin Endpoints ───────────────────────────────────────────────────

    @GetMapping("/api/admin/orders")
    @Tag(name = "Admin - Orders")
    @Operation(summary = "List all orders (ADMIN)")
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/api/admin/orders/{id}/status")
    @Tag(name = "Admin - Orders")
    @Operation(summary = "Update order status (ADMIN)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated.", orderService.updateOrderStatus(id, status)));
    }
}
