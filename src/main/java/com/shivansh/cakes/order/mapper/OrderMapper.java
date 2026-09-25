package com.shivansh.cakes.order.mapper;

import com.shivansh.cakes.order.dto.response.OrderItemResponse;
import com.shivansh.cakes.order.dto.response.OrderResponse;
import com.shivansh.cakes.order.entity.Order;
import com.shivansh.cakes.order.entity.OrderItem;
import com.shivansh.cakes.product.mapper.ProductMapper;
import com.shivansh.cakes.product.repository.ProductImageRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final ProductMapper productMapper;
    private final ProductImageRepository productImageRepository;

    public OrderMapper(ProductMapper productMapper, ProductImageRepository productImageRepository) {
        this.productMapper = productMapper;
        this.productImageRepository = productImageRepository;
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderTotal(),
                order.getShippingCost(),
                order.getDiscount(),
                order.getStatus(),
                order.getDeliveryDate(),
                order.getDeliveryAddress(),
                order.getRazorpayOrderId(),
                order.getRazorpayPaymentId(),
                order.getOrderItems().stream().map(this::toItemResponse).toList()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        var product = item.getProduct();
        var images = productImageRepository.findByProductId(product.getId());

        return new OrderItemResponse(
                item.getId(),
                item.getQuantity(),
                item.getPrice(),
                productMapper.toResponse(product, images)
        );
    }
}
