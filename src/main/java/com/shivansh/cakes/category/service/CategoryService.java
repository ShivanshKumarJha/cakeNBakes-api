package com.shivansh.cakes.category.service;

import com.shivansh.cakes.category.dto.request.CategoryRequest;
import com.shivansh.cakes.category.dto.response.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> findAll();
    CategoryResponse findById(Long id);
    CategoryResponse create(CategoryRequest request, MultipartFile image);
    CategoryResponse update(Long id, CategoryRequest request, MultipartFile image);
    void delete(Long id);
}
