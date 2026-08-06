package com.shivansh.cakes.cart.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CartItem extends BaseEntity {

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double subTotal;
}
