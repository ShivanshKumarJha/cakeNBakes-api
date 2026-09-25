package com.shivansh.cakes.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        String contactNumber,

        // For profile update with multipart, image is sent separately as a file
        String image
) {}
