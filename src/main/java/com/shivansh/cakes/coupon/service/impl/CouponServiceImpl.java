package com.shivansh.cakes.coupon.service.impl;

import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.coupon.dto.request.CouponRequest;
import com.shivansh.cakes.coupon.dto.response.CouponResponse;
import com.shivansh.cakes.coupon.entity.Coupon;
import com.shivansh.cakes.coupon.mapper.CouponMapper;
import com.shivansh.cakes.coupon.repository.CouponRepository;
import com.shivansh.cakes.coupon.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> findAll() {
        return couponRepository.findAll().stream().map(couponMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse findById(Long id) {
        return couponMapper.toResponse(getOrThrow(id));
    }

    @Override
    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCouponCode(request.couponCode())) {
            throw new BusinessException("Coupon code already exists");
        }
        Coupon coupon = new Coupon();
        populate(coupon, request);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    public CouponResponse update(Long id, CouponRequest request) {
        Coupon coupon = getOrThrow(id);
        if (!coupon.getCouponCode().equals(request.couponCode())
                && couponRepository.existsByCouponCode(request.couponCode())) {
            throw new BusinessException("Coupon code already in use");
        }
        populate(coupon, request);
        return couponMapper.toResponse(couponRepository.save(coupon));
    }

    @Override
    public void delete(Long id) {
        couponRepository.delete(getOrThrow(id));
    }

    /**
     * BRS §8.1 coupon rules:
     * - Coupon must exist (404 if not)
     * - Must not be expired (400)
     * - Cart total must meet minimum (400)
     * - Discount = cartTotal * offer / 100 (percentage)
     */
    @Override
    public BigDecimal applyCoupon(String couponCode, BigDecimal cartTotal) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid coupon code"));

        if (coupon.getExpiryDate() != null && LocalDate.now().isAfter(coupon.getExpiryDate())) {
            throw new BusinessException("Coupon expired");
        }

        if (coupon.getMinimum() != null) {
            BigDecimal minimum = BigDecimal.valueOf(coupon.getMinimum());
            if (cartTotal.compareTo(minimum) < 0) {
                throw new BusinessException("Minimum cart total of ₹" + minimum + " not met");
            }
        }

        // Treat discount as percentage
        BigDecimal discountPct = BigDecimal.valueOf(coupon.getDiscount());
        return cartTotal.multiply(discountPct).divide(BigDecimal.valueOf(100));
    }

    private void populate(Coupon coupon, CouponRequest request) {
        coupon.setCouponCode(request.couponCode());
        coupon.setDiscount(request.discount());
        coupon.setDescription(request.description());
        coupon.setMinimum(request.minimum());
        coupon.setExpiryDate(request.expiryDate());
    }

    private Coupon getOrThrow(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + id));
    }
}
