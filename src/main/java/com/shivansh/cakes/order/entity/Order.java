package com.shivansh.cakes.order.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import com.shivansh.cakes.order.entity.type.OrderStatus;
import com.shivansh.cakes.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Order extends BaseEntity {

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderTotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime deliveryDate;

    @Column(length = 200)
    private String deliveryAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}