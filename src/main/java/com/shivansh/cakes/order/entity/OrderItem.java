package com.shivansh.cakes.order.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import com.shivansh.cakes.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_order_product",
                columnNames = {"order_id", "product_id"}
        )
})
public class OrderItem extends BaseEntity {

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
