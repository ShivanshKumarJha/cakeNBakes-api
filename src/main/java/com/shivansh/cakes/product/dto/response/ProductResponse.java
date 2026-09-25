package com.shivansh.cakes.product.dto.response;

import com.shivansh.cakes.category.dto.response.CategoryResponse;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String title,
        String slug,
        String image,
        String description,
        BigDecimal price,
        Boolean special,
        Boolean vegan,
        CategoryResponse category,
        List<String> gallery
) {}
