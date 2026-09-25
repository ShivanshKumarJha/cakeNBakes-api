package com.shivansh.cakes.product.mapper;

import com.shivansh.cakes.category.mapper.CategoryMapper;
import com.shivansh.cakes.product.dto.response.ProductResponse;
import com.shivansh.cakes.product.entity.Product;
import com.shivansh.cakes.product.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;

    public ProductMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public ProductResponse toResponse(Product product, List<ProductImage> gallery) {
        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getSlug(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                product.getSpecial(),
                product.getVegan(),
                categoryMapper.toResponse(product.getCategory()),
                gallery.stream().map(ProductImage::getImageUrl).toList()
        );
    }
}
