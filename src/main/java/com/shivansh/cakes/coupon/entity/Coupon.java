package com.shivansh.cakes.coupon.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Coupon extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String couponCode;

    @Column(scale = 2)
    private Double discount;

    @Column(length = 100)
    private String description;

    @Column(scale = 2)
    private Double minimum;

    private LocalDate expiryDate;
}