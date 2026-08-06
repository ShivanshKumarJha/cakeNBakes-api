package com.shivansh.cakes.product.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends BaseEntity {

    private String title;

    private String slug;

    private String image;

    private String description;

    private Double price;

    private Boolean special = false;

    private Boolean vegan;
}