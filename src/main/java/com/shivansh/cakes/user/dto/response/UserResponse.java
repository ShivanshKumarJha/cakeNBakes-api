package com.shivansh.cakes.user.dto.response;

public record UserResponse(
        Long id,
        String name,
        String email,
        String contactNumber,
        String image,
        Boolean isVerified,
        Boolean status
) {}
