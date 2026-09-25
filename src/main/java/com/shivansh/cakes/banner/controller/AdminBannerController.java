package com.shivansh.cakes.banner.controller;

import com.shivansh.cakes.banner.dto.request.BannerRequest;
import com.shivansh.cakes.banner.dto.response.BannerResponse;
import com.shivansh.cakes.banner.service.BannerService;
import com.shivansh.cakes.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Admin banner CRUD. */
@RestController
@RequestMapping("/api/admin/banners")
@Tag(name = "Admin - Banners", description = "Banner management (ADMIN only)")
public class AdminBannerController {

    private final BannerService bannerService;

    public AdminBannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.findById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create banner (image required)")
    public ResponseEntity<ApiResponse<BannerResponse>> create(
            @Valid @RequestPart("data") BannerRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Banner created.", bannerService.create(request, image)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BannerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("data") BannerRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.success("Banner updated.", bannerService.update(id, request, image)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete banner — fails if only 1 remains (BRS §8.4)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bannerService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Banner deleted."));
    }
}
