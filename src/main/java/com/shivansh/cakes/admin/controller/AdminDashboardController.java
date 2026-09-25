package com.shivansh.cakes.admin.controller;

import com.shivansh.cakes.admin.dto.response.DashboardResponse;
import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.order.repository.OrderRepository;
import com.shivansh.cakes.product.repository.ProductRepository;
import com.shivansh.cakes.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Admin - Dashboard", description = "Admin dashboard statistics")
public class AdminDashboardController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AdminDashboardController(ProductRepository productRepository, OrderRepository orderRepository,
                                    UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get dashboard statistics (revenue, counts, charts)")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardStats() {
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();

        BigDecimal totalRevenue = orderRepository.sumRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal totalDiscount = orderRepository.sumDiscount();
        if (totalDiscount == null) totalDiscount = BigDecimal.ZERO;

        BigDecimal totalShipping = orderRepository.sumShipping();
        if (totalShipping == null) totalShipping = BigDecimal.ZERO;

        // Process charts
        List<Object[]> rawOrdersByMonth = orderRepository.countOrdersByMonth();
        List<Map<String, Object>> ordersByMonth = rawOrdersByMonth.stream()
                .map(row -> Map.<String, Object>of("month", row[0], "count", row[1]))
                .collect(Collectors.toList());

        List<Object[]> rawOrdersByCategory = productRepository.countOrdersByCategory();
        List<Map<String, Object>> ordersByCategory = rawOrdersByCategory.stream()
                .map(row -> Map.<String, Object>of("category", row[0], "count", row[1]))
                .collect(Collectors.toList());

        DashboardResponse response = new DashboardResponse(
                totalProducts, totalOrders, totalUsers,
                totalRevenue, totalDiscount, totalShipping,
                ordersByMonth, ordersByCategory
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
