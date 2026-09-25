package com.shivansh.cakes.product.controller;

import com.shivansh.cakes.common.response.ApiResponse;
import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.product.dto.response.ProductResponse;
import com.shivansh.cakes.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Public product browsing endpoints. */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Public product browsing")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List/search/filter products — supports ?categorySlug, ?vegan, ?q, ?page, ?size")
    public ResponseEntity<PageResponse<ProductResponse>> findAll(
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) Boolean vegan,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(productService.findAll(categorySlug, vegan, q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.findById(id)));
    }
}