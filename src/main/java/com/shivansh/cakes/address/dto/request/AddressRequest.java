package com.shivansh.cakes.address.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 25, message = "Name must not exceed 25 characters")
        String name,

        @NotBlank(message = "House name is required")
        @Size(max = 25, message = "House name must not exceed 25 characters")
        String houseName,

        @NotBlank(message = "Street is required")
        @Size(max = 50, message = "Street must not exceed 50 characters")
        String street,

        String landMark,

        @NotBlank(message = "Pin code is required")
        String pinCode,

        @NotBlank(message = "District is required")
        String district,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Contact is required")
        String contact
) {}
