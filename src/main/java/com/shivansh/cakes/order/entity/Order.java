package com.shivansh.cakes.order.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Order extends BaseEntity {

    @Column(scale = 2)
    private Double orderTotal;

    @Column(scale = 2)
    private Double shippingCost;

    @Column(scale = 2)
    private Double discount;

    private String status;

    private LocalDateTime deliveryDate;

    @Column(length = 200)
    private String deliveryAddress;
}