package com.shivansh.cakes.admin.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalProducts,
        long totalOrders,
        long totalUsers,
        BigDecimal totalRevenue,
        BigDecimal totalDiscount,
        BigDecimal totalShipping,
        List<Map<String, Object>> ordersByMonth,
        List<Map<String, Object>> ordersByCategory
) {}
