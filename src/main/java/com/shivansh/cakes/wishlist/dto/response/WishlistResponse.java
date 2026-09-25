package com.shivansh.cakes.wishlist.dto.response;

import java.util.List;

public record WishlistResponse(
        Long id,
        List<WishlistItemResponse> items,
        Integer itemCount
) {}
