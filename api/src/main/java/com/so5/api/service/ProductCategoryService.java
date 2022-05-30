package com.so5.api.service;

import com.so5.api.entity.ProductCategory;
import com.so5.api.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public List<ProductCategory> findAll() {

        return productCategoryRepository.findAll();
    }
}
