package com.shivansh.cakes.wishlist.service;

import com.shivansh.cakes.wishlist.dto.request.WishlistItemRequest;
import com.shivansh.cakes.wishlist.dto.response.WishlistResponse;

public interface WishlistService {
    WishlistResponse getWishlist(String email);
    WishlistResponse addToWishlist(String email, WishlistItemRequest request);
    WishlistResponse removeFromWishlist(String email, Long productId);
    void clearWishlist(String email);
}
