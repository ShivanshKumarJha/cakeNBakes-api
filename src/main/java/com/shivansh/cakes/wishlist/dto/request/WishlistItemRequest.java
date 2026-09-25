package com.shivansh.cakes.wishlist.dto.request;

import jakarta.validation.constraints.NotNull;

public record WishlistItemRequest(
        @NotNull(message = "Product ID is required")
        Long productId
) {}
