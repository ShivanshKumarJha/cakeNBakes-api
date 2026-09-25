package com.shivansh.cakes.wishlist.controller;

import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.wishlist.dto.request.WishlistItemRequest;
import com.shivansh.cakes.wishlist.dto.response.WishlistResponse;
import com.shivansh.cakes.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "User wishlist management")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get user wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(principal.getName())));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            Principal principal,
            @Valid @RequestBody WishlistItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Item added to wishlist.",
                wishlistService.addToWishlist(principal.getName(), request)));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeFromWishlist(
            Principal principal,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Item removed from wishlist.",
                wishlistService.removeFromWishlist(principal.getName(), productId)));
    }
}
