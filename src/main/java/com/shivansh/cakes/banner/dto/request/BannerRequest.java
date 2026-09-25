package com.shivansh.cakes.banner.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BannerRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 50, message = "Title must not exceed 50 characters")
        String title,

        @Size(max = 100, message = "Caption must not exceed 100 characters")
        String caption,

        String category
) {}
