package com.shivansh.cakes.category.dto.response;

public record CategoryResponse(
        Long id,
        String title,
        String slug,
        String description,
        String image
) {}
