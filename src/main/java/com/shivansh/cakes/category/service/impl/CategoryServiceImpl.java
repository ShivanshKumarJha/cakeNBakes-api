package com.shivansh.cakes.category.service.impl;

import com.shivansh.cakes.category.dto.request.CategoryRequest;
import com.shivansh.cakes.category.dto.response.CategoryResponse;
import com.shivansh.cakes.category.entity.Category;
import com.shivansh.cakes.category.mapper.CategoryMapper;
import com.shivansh.cakes.category.repository.CategoryRepository;
import com.shivansh.cakes.category.service.CategoryService;
import com.shivansh.cakes.common.exception.BusinessException;
import com.shivansh.cakes.common.exception.ResourceNotFoundException;
import com.shivansh.cakes.common.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService fileStorageService;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            FileStorageService fileStorageService
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return categoryMapper.toResponse(getOrThrow(id));
    }

    @Override
    public CategoryResponse create(CategoryRequest request, MultipartFile image) {
        String slug = resolveSlug(request.slug(), request.title());

        if (categoryRepository.existsByTitle(request.title())) {
            throw new BusinessException("Category with this title already exists");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("Category with this slug already exists");
        }

        Category category = new Category();
        category.setTitle(request.title());
        category.setSlug(slug);
        category.setDescription(request.description());

        if (image != null && !image.isEmpty()) {
            category.setImage(fileStorageService.store(image, "category-img"));
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request, MultipartFile image) {
        Category category = getOrThrow(id);

        String slug = resolveSlug(request.slug(), request.title());
        if (!category.getSlug().equals(slug) && categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("Slug already in use");
        }

        category.setTitle(request.title());
        category.setSlug(slug);
        category.setDescription(request.description());

        if (image != null && !image.isEmpty()) {
            fileStorageService.delete(category.getImage());
            category.setImage(fileStorageService.store(image, "category-img"));
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = getOrThrow(id);
        fileStorageService.delete(category.getImage());
        categoryRepository.delete(category);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private String resolveSlug(String provided, String title) {
        if (provided != null && !provided.isBlank()) {
            return provided.toLowerCase().trim();
        }
        return title.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
    }
}
