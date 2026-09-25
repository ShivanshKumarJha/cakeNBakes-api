package com.shivansh.cakes.banner.dto.response;

public record BannerResponse(
        Long id,
        String banner,
        String title,
        String caption,
        String category
) {}
