package com.so5.api.controller;

import com.so5.api.entity.ProductCategory;
import com.so5.api.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping("/")
    public List<ProductCategory> findAll() {

        return productCategoryService.findAll();
    }
}
