package com.shivansh.cakes.banner.controller;

import com.shivansh.cakes.banner.dto.response.BannerResponse;
import com.shivansh.cakes.banner.service.BannerService;
import com.shivansh.cakes.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public banner listing for homepage. */
@RestController
@RequestMapping("/api/banners")
@Tag(name = "Banners", description = "Public banner listing")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    @Operation(summary = "Get all banners (public)")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.findAll()));
    }
}
