package com.shivansh.cakes.category.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import com.shivansh.cakes.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category extends BaseEntity {

    @Column(unique = true)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(length = 100)
    private String description;

    private String image;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}