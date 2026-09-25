package com.shivansh.cakes.cart.controller;

import com.shivansh.cakes.cart.dto.request.CartItemRequest;
import com.shivansh.cakes.cart.dto.response.CartResponse;
import com.shivansh.cakes.cart.service.CartService;
import com.shivansh.cakes.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get user cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(principal.getName())));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            Principal principal,
            @Valid @RequestBody CartItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Item added to cart.",
                cartService.addToCart(principal.getName(), request)));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Update item quantity in cart")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            Principal principal,
            @PathVariable Long productId,
            @Valid @RequestBody CartItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cart updated.",
                cartService.updateCartItem(principal.getName(), productId, request)));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            Principal principal,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart.",
                cartService.removeFromCart(principal.getName(), productId)));
    }
}
