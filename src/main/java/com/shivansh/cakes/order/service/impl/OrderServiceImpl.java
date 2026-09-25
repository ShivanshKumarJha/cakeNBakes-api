package com.shivansh.cakes.order.service.impl;

import com.shivansh.cakes.address.entity.Address;
import com.shivansh.cakes.address.repository.AddressRepository;
import com.shivansh.cakes.cart.entity.Cart;
import com.shivansh.cakes.cart.repository.CartItemRepository;
import com.shivansh.cakes.cart.repository.CartRepository;
import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.coupon.service.CouponService;
import com.shivansh.cakes.order.dto.request.OrderRequest;
import com.shivansh.cakes.order.dto.response.OrderResponse;
import com.shivansh.cakes.order.entity.Order;
import com.shivansh.cakes.order.entity.OrderItem;
import com.shivansh.cakes.order.entity.type.OrderStatus;
import com.shivansh.cakes.order.mapper.OrderMapper;
import com.shivansh.cakes.order.repository.OrderRepository;
import com.shivansh.cakes.order.service.OrderService;
import com.shivansh.cakes.payment.RazorpayService;
import com.shivansh.cakes.user.entity.User;
import com.shivansh.cakes.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final RazorpayService razorpayService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository,
                            CartItemRepository cartItemRepository, AddressRepository addressRepository,
                            UserRepository userRepository, CouponService couponService,
                            RazorpayService razorpayService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
        this.razorpayService = razorpayService;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponse createOrder(String email, OrderRequest request) {
        User user = getUserOrThrow(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Cart is empty"));

        if (cart.getCartItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Address does not belong to user");
        }

        // Calculate totals
        BigDecimal subTotal = cart.getCartItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            discount = couponService.applyCoupon(request.couponCode(), subTotal);
        }

        // Simple shipping logic for example (e.g. 50 flat rate)
        BigDecimal shippingCost = BigDecimal.valueOf(50);
        BigDecimal orderTotal = subTotal.subtract(discount).add(shippingCost);

        // Create Razorpay Order
        String razorpayOrderId = razorpayService.createOrder(orderTotal);

        Order order = new Order();
        order.setUser(user);
        order.setOrderTotal(orderTotal);
        order.setShippingCost(shippingCost);
        order.setDiscount(discount);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(formatAddress(address));
        order.setRazorpayOrderId(razorpayOrderId);

        for (var cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            order.getOrderItems().add(orderItem);
        }

        Order saved = orderRepository.save(order);
        
        // Clear cart
        cartItemRepository.deleteByCartId(cart.getId());
        
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String email) {
        User user = getUserOrThrow(email);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(orderMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String email, Long id) {
        Order order = getOrderOrThrow(id);
        User user = getUserOrThrow(email);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Order does not belong to user");
        }
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(String email, Long id) {
        Order order = getOrderOrThrow(id);
        User user = getUserOrThrow(email);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Order does not belong to user");
        }
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot cancel order in current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse verifyPayment(String email, Long id, String paymentId, String signature) {
        Order order = getOrderOrThrow(id);
        User user = getUserOrThrow(email);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Order does not belong to user");
        }

        if (!razorpayService.verifySignature(order.getRazorpayOrderId(), paymentId, signature)) {
            throw new BusinessException("Invalid payment signature");
        }

        order.setRazorpayPaymentId(paymentId);
        order.setRazorpaySignature(signature);
        order.setStatus(OrderStatus.CONFIRMED);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent().stream().map(orderMapper::toResponse).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderOrThrow(id);
        order.setStatus(status);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private String formatAddress(Address a) {
        return String.format("%s, %s, %s, %s, %s, %s - %s", 
                a.getName(), a.getHouseName(), a.getStreet(), a.getDistrict(), a.getState(), a.getCountry(), a.getPinCode());
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
