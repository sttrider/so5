package com.so5.api.controller;

import com.so5.api.entity.ProductCategory;
import com.so5.api.repository.ProductCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @BeforeEach
    void deleteAll() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE address");
        jdbcTemplate.execute("TRUNCATE TABLE credit_card_data");
        jdbcTemplate.execute("TRUNCATE TABLE customer");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE product_category");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Container
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0.29");

    @Container
    private static final LocalStackContainer LOCAL_STACK_CONTAINER =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(S3);

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("aws.server.host", () -> LOCAL_STACK_CONTAINER.getEndpointOverride(S3));
    }

    public ProductCategory createProductCategory(String name) {
        return productCategoryRepository.save(ProductCategory.builder().name(name).build());
    }
}
