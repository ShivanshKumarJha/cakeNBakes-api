package com.shivansh.cakes.coupon.mapper;

import com.shivansh.cakes.coupon.dto.response.CouponResponse;
import com.shivansh.cakes.coupon.entity.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
    public CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCouponCode(),
                coupon.getDiscount(),
                coupon.getDescription(),
                coupon.getMinimum(),
                coupon.getExpiryDate()
        );
    }
}
