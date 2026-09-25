package com.shivansh.cakes.cart.service;

import com.shivansh.cakes.cart.dto.request.CartItemRequest;
import com.shivansh.cakes.cart.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(String email);
    CartResponse addToCart(String email, CartItemRequest request);
    CartResponse updateCartItem(String email, Long productId, CartItemRequest request);
    CartResponse removeFromCart(String email, Long productId);
    void clearCart(String email);
}
