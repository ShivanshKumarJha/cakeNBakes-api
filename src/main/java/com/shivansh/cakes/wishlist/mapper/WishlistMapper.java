package com.shivansh.cakes.wishlist.mapper;

import com.shivansh.cakes.product.mapper.ProductMapper;
import com.shivansh.cakes.product.repository.ProductImageRepository;
import com.shivansh.cakes.wishlist.dto.response.WishlistItemResponse;
import com.shivansh.cakes.wishlist.dto.response.WishlistResponse;
import com.shivansh.cakes.wishlist.entity.Wishlist;
import com.shivansh.cakes.wishlist.entity.WishlistItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WishlistMapper {

    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;

    public WishlistMapper(ProductMapper productMapper, ProductImageRepository productImageRepository) {
        this.productMapper = productMapper;
        this.productImageRepository = productImageRepository;
    }

    public WishlistResponse toResponse(Wishlist wishlist) {
        List<WishlistItemResponse> items = wishlist.getWishlistItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new WishlistResponse(wishlist.getId(), items, items.size());
    }

    private WishlistItemResponse toItemResponse(WishlistItem item) {
        var product = item.getProduct();
        var images = productImageRepository.findByProductId(product.getId());

        return new WishlistItemResponse(
                item.getId(),
                productMapper.toResponse(product, images)
        );
    }
}
