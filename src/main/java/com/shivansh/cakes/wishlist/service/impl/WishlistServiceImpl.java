package com.shivansh.cakes.wishlist.service.impl;

import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.product.entity.Product;
import com.shivansh.cakes.product.repository.ProductRepository;
import com.shivansh.cakes.user.entity.User;
import com.shivansh.cakes.user.repository.UserRepository;
import com.shivansh.cakes.wishlist.dto.request.WishlistItemRequest;
import com.shivansh.cakes.wishlist.dto.response.WishlistResponse;
import com.shivansh.cakes.wishlist.entity.Wishlist;
import com.shivansh.cakes.wishlist.entity.WishlistItem;
import com.shivansh.cakes.wishlist.mapper.WishlistMapper;
import com.shivansh.cakes.wishlist.repository.WishlistItemRepository;
import com.shivansh.cakes.wishlist.repository.WishlistRepository;
import com.shivansh.cakes.wishlist.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               WishlistItemRepository wishlistItemRepository,
                               UserRepository userRepository,
                               ProductRepository productRepository,
                               WishlistMapper wishlistMapper) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    public WishlistResponse getWishlist(String email) {
        return wishlistMapper.toResponse(getOrCreateWishlist(email));
    }

    @Override
    public WishlistResponse addToWishlist(String email, WishlistItemRequest request) {
        Wishlist wishlist = getOrCreateWishlist(email);
        Product product = getProductOrThrow(request.productId());

        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), product.getId())) {
            WishlistItem item = new WishlistItem();
            item.setProduct(product);
            wishlist.addWishlistItem(item);
            wishlist = wishlistRepository.save(wishlist);
        }

        return wishlistMapper.toResponse(wishlist);
    }

    @Override
    public WishlistResponse removeFromWishlist(String email, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(email);
        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        wishlist.getWishlistItems().removeIf(i -> i.getProduct().getId().equals(productId));
        return wishlistMapper.toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    public void clearWishlist(String email) {
        Wishlist wishlist = getOrCreateWishlist(email);
        wishlist.getWishlistItems().clear();
        wishlistRepository.save(wishlist);
    }

    private Wishlist getOrCreateWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return wishlistRepository.findByUserId(user.getId()).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(user);
            return wishlistRepository.save(newWishlist);
        });
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
