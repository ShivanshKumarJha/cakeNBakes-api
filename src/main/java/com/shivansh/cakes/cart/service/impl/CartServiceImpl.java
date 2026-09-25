package com.shivansh.cakes.cart.service.impl;

import com.shivansh.cakes.cart.dto.request.CartItemRequest;
import com.shivansh.cakes.cart.dto.response.CartResponse;
import com.shivansh.cakes.cart.entity.Cart;
import com.shivansh.cakes.cart.entity.CartItem;
import com.shivansh.cakes.cart.mapper.CartMapper;
import com.shivansh.cakes.cart.repository.CartItemRepository;
import com.shivansh.cakes.cart.repository.CartRepository;
import com.shivansh.cakes.cart.service.CartService;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.product.entity.Product;
import com.shivansh.cakes.product.repository.ProductRepository;
import com.shivansh.cakes.user.entity.User;
import com.shivansh.cakes.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
                           UserRepository userRepository, ProductRepository productRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartResponse getCart(String email) {
        return cartMapper.toResponse(getOrCreateCart(email));
    }

    @Override
    public CartResponse addToCart(String email, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        Product product = getProductOrThrow(request.productId());

        Optional<CartItem> existingOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        CartItem item;
        if (existingOpt.isPresent()) {
            item = existingOpt.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            item.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(request.quantity());
            item.setWeight(request.weight());
            item.setPrice(product.getPrice());
            item.setSubTotal(product.getPrice().multiply(BigDecimal.valueOf(request.quantity())));
            cart.addCartItem(item);
        }

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateCartItem(String email, Long productId, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        item.setQuantity(request.quantity());
        item.setWeight(request.weight());
        item.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(request.quantity())));

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse removeFromCart(String email, Long productId) {
        Cart cart = getOrCreateCart(email);
        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        // Refresh cart to return updated response
        cart.getCartItems().removeIf(i -> i.getProduct().getId().equals(productId));
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
