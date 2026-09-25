package com.shivansh.cakes.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Title is required")
        String title,

        String slug,

        @Size(max = 100, message = "Description cannot exceed 100 characters")
        String description
) {}
