package com.so5.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.so5.api.entity.Product;
import com.so5.api.entity.ProductCategory;
import com.so5.api.repository.ProductRepository;
import com.so5.api.vo.SearchProductVO;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        createProductCategory("category 1");
    }

    @Test
    public void whenFindBySku_thenShouldReturnProduct() throws Exception {

        var product = createProduct();

        mockMvc.perform(get("/product/{sku}", product.getSku()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.price", is(product.getPrice())))
                .andExpect(jsonPath("$.inventory", is(product.getInventory())))
                .andExpect(jsonPath("$.shipmentDeliveryTimes", is(product.getShipmentDeliveryTimes())))
                .andExpect(jsonPath("$.enabled", is(product.isEnabled())))
                .andExpect(jsonPath("$.category.id", is(product.getCategory().getId().intValue())));
    }

    @Test
    void whenFindByNonExistingSku_thenShouldReturnNotFound() throws Exception {

        mockMvc.perform(get("/product/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Not found."));
    }

    @Test
    public void whenSearch_thenShouldReturnAllActiveProducts() throws Exception {

        createProductCategory("category 2");
        var product = createProduct(1, true, 1L);
        var product2 = createProduct(2, true, 1L);
        createProduct(3, false, 1L);
        createProduct(4, true, 2L);

        var searchProductVO = SearchProductVO.builder().categoryId(1L).build();

        mockMvc.perform(post("/product/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductVO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(product2.getId().intValue())));
    }

    @Test
    public void whenSearchWithEmptyCategory_thenShouldReturnEmptyArray() throws Exception {

        var newCategory = createProductCategory("category 2");
        createProduct(1, true, 1L);

        var searchProductVO = SearchProductVO.builder().categoryId(newCategory.getId()).build();

        mockMvc.perform(post("/product/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductVO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Ignore
    public void whenList_thenShouldReturnAllProducts() throws Exception {
        createProduct();

        var searchProductVO = SearchProductVO.builder().categoryId(1L).build();
        mockMvc.perform(post("/product/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductVO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    private Product createProduct() {

        return createProduct(1, true, 1L);
    }

    private Product createProduct(int count, boolean enabled, Long categoryId) {
        return productRepository.save(Product.builder()
                .sku("sku" + count)
                .name("Product Sku " + count)
                .description("Product description " + count)
                .price(10D)
                .inventory(1)
                .shipmentDeliveryTimes(1)
                .enabled(enabled)
                .category(ProductCategory.builder()
                        .id(categoryId)
                        .build())
                .creationDate(LocalDateTime.now())
                .build());
    }
}
