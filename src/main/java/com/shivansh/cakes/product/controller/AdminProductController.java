package com.shivansh.cakes.product.controller;

import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.product.dto.request.ProductRequest;
import com.shivansh.cakes.product.dto.response.ProductResponse;
import com.shivansh.cakes.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Admin-only product CRUD. */
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin - Products", description = "Product management (ADMIN only)")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<PageResponse<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.findAll(null, null, null, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single product")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.findById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create product")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestPart("data") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created.", productService.create(request, image)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("data") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(ApiResponse.success("Product updated.", productService.update(id, request, image)));
    }

    @PostMapping(value = "/{id}/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add gallery images (multi-file)")
    public ResponseEntity<ApiResponse<ProductResponse>> addGallery(
            @PathVariable Long id,
            @RequestPart("images") MultipartFile[] images
    ) {
        return ResponseEntity.ok(ApiResponse.success("Gallery updated.", productService.addGalleryImages(id, images)));
    }

    @DeleteMapping("/{id}/gallery/{imageId}")
    @Operation(summary = "Delete a gallery image")
    public ResponseEntity<ApiResponse<Void>> deleteGalleryImage(
            @PathVariable Long id,
            @PathVariable Long imageId
    ) {
        productService.deleteGalleryImage(id, imageId);
        return ResponseEntity.ok(ApiResponse.success("Gallery image deleted."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product and all its images")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted."));
    }
}
