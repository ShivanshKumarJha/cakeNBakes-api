package com.shivansh.cakes.coupon.controller;

import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.coupon.dto.request.CouponRequest;
import com.shivansh.cakes.coupon.dto.response.CouponResponse;
import com.shivansh.cakes.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@Tag(name = "Admin - Coupons", description = "Coupon management (ADMIN only)")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(couponService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(couponService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Create coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created.", couponService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Coupon updated.", couponService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted."));
    }
}
