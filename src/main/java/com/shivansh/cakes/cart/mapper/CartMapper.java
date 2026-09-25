package com.shivansh.cakes.cart.mapper;

import com.shivansh.cakes.cart.dto.response.CartItemResponse;
import com.shivansh.cakes.cart.dto.response.CartResponse;
import com.shivansh.cakes.cart.entity.Cart;
import com.shivansh.cakes.cart.entity.CartItem;
import com.shivansh.cakes.product.mapper.ProductMapper;
import com.shivansh.cakes.product.repository.ProductImageRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;

    public CartMapper(ProductMapper productMapper, ProductImageRepository productImageRepository) {
        this.productMapper = productMapper;
        this.productImageRepository = productImageRepository;
    }

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer count = items.stream()
                .map(CartItemResponse::quantity)
                .reduce(0, Integer::sum);

        return new CartResponse(cart.getId(), items, total, count);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        var product = item.getProduct();
        var images = productImageRepository.findByProductId(product.getId());

        return new CartItemResponse(
                item.getId(),
                item.getQuantity(),
                item.getWeight(),
                item.getPrice(),
                item.getSubTotal(),
                productMapper.toResponse(product, images)
        );
    }
}
