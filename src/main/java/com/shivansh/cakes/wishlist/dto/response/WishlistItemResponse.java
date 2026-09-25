package com.shivansh.cakes.wishlist.dto.response;

import com.shivansh.cakes.product.dto.response.ProductResponse;

public record WishlistItemResponse(
        Long id,
        ProductResponse product
) {}
