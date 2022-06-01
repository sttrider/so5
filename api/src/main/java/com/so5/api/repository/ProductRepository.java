package com.so5.api.repository;

import com.so5.api.entity.Product;
import com.so5.api.entity.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryAndEnabled(ProductCategory category, boolean enabled, Pageable pageable);

    List<Product> findByCategory(ProductCategory category, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.enabled = :enabled WHERE p.sku = :sku")
    void updateStatusBySku(boolean enabled, String sku);
}
