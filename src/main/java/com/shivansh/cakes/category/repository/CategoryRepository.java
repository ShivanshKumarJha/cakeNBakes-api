package com.shivansh.cakes.category.repository;

import com.shivansh.cakes.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByTitle(String title);
    boolean existsByTitle(String title);
    boolean existsBySlug(String slug);
}
