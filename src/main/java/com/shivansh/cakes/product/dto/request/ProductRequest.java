package com.shivansh.cakes.product.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        Boolean special,

        @NotNull(message = "Vegan flag is required")
        Boolean vegan
) {}
