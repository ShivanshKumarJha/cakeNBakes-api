package com.shivansh.cakes.product.service.impl;

import com.shivansh.cakes.category.entity.Category;
import com.shivansh.cakes.category.repository.CategoryRepository;
import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.common.response.PageResponse;
import com.shivansh.cakes.common.service.FileStorageService;
import com.shivansh.cakes.product.dto.request.ProductRequest;
import com.shivansh.cakes.product.dto.response.ProductResponse;
import com.shivansh.cakes.product.entity.Product;
import com.shivansh.cakes.product.entity.ProductImage;
import com.shivansh.cakes.product.mapper.ProductMapper;
import com.shivansh.cakes.product.repository.ProductImageRepository;
import com.shivansh.cakes.product.repository.ProductRepository;
import com.shivansh.cakes.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            CategoryRepository categoryRepository,
            ProductMapper productMapper,
            FileStorageService fileStorageService
    ) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(String categorySlug, Boolean vegan, String search, Pageable pageable) {
        Page<Product> page;

        if (search != null && !search.isBlank()) {
            page = productRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else if (categorySlug != null) {
            Category category = categoryRepository.findBySlug(categorySlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categorySlug));
            page = productRepository.findByCategoryId(category.getId(), pageable);
        } else if (Boolean.TRUE.equals(vegan)) {
            page = productRepository.findByVeganTrue(pageable);
        } else {
            page = productRepository.findAll(pageable);
        }

        List<ProductResponse> content = page.getContent().stream()
                .map(p -> productMapper.toResponse(p, productImageRepository.findByProductId(p.getId())))
                .toList();

        return new PageResponse<>(content, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = getOrThrow(id);
        return productMapper.toResponse(product, productImageRepository.findByProductId(id));
    }

    @Override
    public ProductResponse create(ProductRequest request, MultipartFile image) {
        Category category = getCategoryOrThrow(request.categoryId());

        String slug = generateSlug(request.title());
        if (productRepository.existsBySlug(slug)) {
            throw new BusinessException("A product with this slug already exists");
        }

        Product product = new Product();
        product.setTitle(request.title());
        product.setSlug(slug);
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSpecial(Boolean.TRUE.equals(request.special()));
        product.setVegan(request.vegan());
        product.setCategory(category);

        if (image != null && !image.isEmpty()) {
            product.setImage(fileStorageService.store(image, "product-img"));
        }

        return productMapper.toResponse(productRepository.save(product), List.of());
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request, MultipartFile image) {
        Product product = getOrThrow(id);
        Category category = getCategoryOrThrow(request.categoryId());

        String slug = generateSlug(request.title());
        if (!product.getSlug().equals(slug) && productRepository.existsBySlug(slug)) {
            throw new BusinessException("A product with this slug already exists");
        }

        product.setTitle(request.title());
        product.setSlug(slug);
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSpecial(Boolean.TRUE.equals(request.special()));
        product.setVegan(request.vegan());
        product.setCategory(category);

        if (image != null && !image.isEmpty()) {
            fileStorageService.delete(product.getImage());
            product.setImage(fileStorageService.store(image, "product-img"));
        }

        return productMapper.toResponse(productRepository.save(product),
                productImageRepository.findByProductId(id));
    }

    @Override
    public void delete(Long id) {
        Product product = getOrThrow(id);
        // Delete gallery images from disk
        productImageRepository.findByProductId(id)
                .forEach(img -> fileStorageService.delete(img.getImageUrl()));
        productImageRepository.deleteByProductId(id);
        fileStorageService.delete(product.getImage());
        productRepository.delete(product);
    }

    @Override
    public ProductResponse addGalleryImages(Long id, MultipartFile[] images) {
        Product product = getOrThrow(id);
        Arrays.stream(images)
                .filter(f -> f != null && !f.isEmpty())
                .forEach(file -> {
                    String url = fileStorageService.store(file, "product-img");
                    productImageRepository.save(ProductImage.builder()
                            .product(product)
                            .imageUrl(url)
                            .build());
                });
        return productMapper.toResponse(product, productImageRepository.findByProductId(id));
    }

    @Override
    public void deleteGalleryImage(Long productId, Long imageId) {
        getOrThrow(productId); // verify product exists
        ProductImage img = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery image not found: " + imageId));
        fileStorageService.delete(img.getImageUrl());
        productImageRepository.delete(img);
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private String generateSlug(String title) {
        return title.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
    }
}
