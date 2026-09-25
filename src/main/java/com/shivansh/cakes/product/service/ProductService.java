package com.shivansh.cakes.product.service;

import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.product.dto.request.ProductRequest;
import com.shivansh.cakes.product.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    PageResponse<ProductResponse> findAll(String categorySlug, Boolean vegan, String search, Pageable pageable);

    ProductResponse findById(Long id);

    // Admin CRUD
    ProductResponse create(ProductRequest request, MultipartFile image);
    ProductResponse update(Long id, ProductRequest request, MultipartFile image);
    void delete(Long id);

    // Gallery
    ProductResponse addGalleryImages(Long id, MultipartFile[] images);
    void deleteGalleryImage(Long productId, Long imageId);
}
