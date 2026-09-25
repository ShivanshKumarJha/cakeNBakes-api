package com.shivansh.cakes.order.repository;

import com.shivansh.cakes.order.entity.Order;
import com.shivansh.cakes.order.entity.type.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    long countByStatus(OrderStatus status);

    @Query("SELECT SUM(o.orderTotal) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal sumRevenue();

    @Query("SELECT SUM(o.discount) FROM Order o")
    BigDecimal sumDiscount();

    @Query("SELECT SUM(o.shippingCost) FROM Order o")
    BigDecimal sumShipping();

    /** For dashboard: orders by month (last 12 months). */
    @Query(value = """
            SELECT TO_CHAR(created_at, 'Mon') AS month,
                   COUNT(*) AS count
            FROM orders
            WHERE created_at >= NOW() - INTERVAL '12 months'
            GROUP BY TO_CHAR(created_at, 'Mon'), DATE_TRUNC('month', created_at)
            ORDER BY DATE_TRUNC('month', created_at)
            """, nativeQuery = true)
    List<Object[]> countOrdersByMonth();

    /** For admin: 5 most recent orders with product + user info. */
    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.orderItems oi JOIN FETCH oi.product ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(org.springframework.data.domain.Pageable pageable);
}
