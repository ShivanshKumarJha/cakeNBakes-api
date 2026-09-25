package com.shivansh.cakes.product.repository;

import com.shivansh.cakes.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByVeganTrue(Pageable pageable);
    Page<Product> findBySpecialTrue(Pageable pageable);
    Page<Product> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /** Count products for admin dashboard. */
    long count();

    /** Used by AdminDashboardController for orders-by-category chart. */
    @Query("SELECT p.category.title, COUNT(oi) FROM OrderItem oi JOIN oi.product p GROUP BY p.category.title")
    List<Object[]> countOrdersByCategory();
}
