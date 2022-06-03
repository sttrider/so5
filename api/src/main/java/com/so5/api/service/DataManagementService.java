package com.so5.api.service;

import com.so5.api.vo.ProductSaveVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataManagementService {

    private final JdbcTemplate jdbcTemplate;
    private final ProductService productService;

    private final ProductCategoryService productCategoryService;

    public void generateData() {
        log.info("Starting import process");
        var categories = productCategoryService.findAll();
        log.info("Found {} categories", categories.size());
        var random = new Random();
        for (int i = 0; i < 300; i++) {
            log.info("Creating product {}", i);
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("images/" + random.nextInt(1, 15) + ".jpg")) {
                var bytes = in.readAllBytes();
                var imageBase64 = "data:image/jpg;base64," + Base64.getEncoder().encodeToString(bytes);
                productService.save(ProductSaveVO.builder()
                        .sku(UUID.randomUUID().toString())
                        .name("Product Sku " + i)
                        .description("Product description " + i)
                        .price(random.nextDouble(500.0))
                        .inventory(random.nextInt(0, 100))
                        .shipmentDeliveryTimes(random.nextInt(2, 6))
                        .enabled(random.nextBoolean())
                        .image(imageBase64)
                        .categoryId(categories.get(random.nextInt(0, categories.size())).getId())
                        .build());
            } catch (Exception e) {
                log.error("Error importing", e);
            }
        }
    }

    public void resetData() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
