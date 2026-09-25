package com.shivansh.cakes.category.mapper;

import com.shivansh.cakes.category.dto.response.CategoryResponse;
import com.shivansh.cakes.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getTitle(),
                category.getSlug(),
                category.getDescription(),
                category.getImage()
        );
    }
}
