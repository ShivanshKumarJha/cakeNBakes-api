package com.shivansh.cakes.coupon.service;

import com.shivansh.cakes.coupon.dto.request.CouponRequest;
import com.shivansh.cakes.coupon.dto.response.CouponResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<CouponResponse> findAll();
    CouponResponse findById(Long id);
    CouponResponse create(CouponRequest request);
    CouponResponse update(Long id, CouponRequest request);
    void delete(Long id);

    /**
     * Applies coupon to cart total per BRS §8.1.
     *
     * @param couponCode the code to validate
     * @param cartTotal  current cart subtotal
     * @return the discount amount to subtract
     */
    BigDecimal applyCoupon(String couponCode, BigDecimal cartTotal);
}
