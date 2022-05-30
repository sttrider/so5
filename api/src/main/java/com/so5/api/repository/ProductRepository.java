package com.so5.api.repository;

import com.so5.api.entity.Product;
import com.so5.api.entity.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(ProductCategory category, Pageable pageable);
}
