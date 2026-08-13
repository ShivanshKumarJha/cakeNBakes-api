package com.shivansh.cakes.product.entity;

import com.shivansh.cakes.category.entity.Category;
import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.*;
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
public class Product extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    private String image;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean special = false;

    @Column(nullable = false)
    private Boolean vegan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}