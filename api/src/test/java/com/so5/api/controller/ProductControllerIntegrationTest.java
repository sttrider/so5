package com.so5.api.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.OpenIdClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockJwtAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.so5.api.config.properties.AwsProperties;
import com.so5.api.entity.*;
import com.so5.api.repository.AddressRepository;
import com.so5.api.repository.CreditCardDataRepository;
import com.so5.api.repository.CustomerRepository;
import com.so5.api.repository.ProductRepository;
import com.so5.api.vo.ProductSaveVO;
import com.so5.api.vo.SearchProductVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private AwsProperties awsProperties;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CreditCardDataRepository creditCardDataRepository;

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
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenList_thenShouldReturnAllProducts() throws Exception {
        createProductCategory("category 2");
        var product = createProduct(1, true, 1L);
        var product2 = createProduct(2, true, 1L);
        var product3 = createProduct(3, false, 1L);
        createProduct(4, true, 2L);

        var searchProductVO = SearchProductVO.builder().categoryId(1L).build();
        mockMvc.perform(post("/product/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductVO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", is(product2.getId().intValue())))
                .andExpect(jsonPath("$.[2].id", is(product3.getId().intValue())));
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenListWithEmptyCategory_thenShouldReturnEmptyArray() throws Exception {

        var newCategory = createProductCategory("category 2");
        createProduct(1, true, 1L);

        var searchProductVO = SearchProductVO.builder().categoryId(newCategory.getId()).build();

        mockMvc.perform(post("/product/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchProductVO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenChangeStatus_thenShouldUpdateStatus() throws Exception {

        var product = createProduct();

        assertThat(product.isEnabled()).isTrue();
        mockMvc.perform(put("/product/{sku}/{enabled}", product.getSku(), false))
                .andExpect(status().isOk());

        var updatedProduct = productRepository.findById(product.getId()).get();
        assertThat(updatedProduct.isEnabled()).isFalse();
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenDelete_thenShouldDelete() throws Exception {

        var product = createProduct();

        var notDeletedProduct = productRepository.findById(product.getId());
        assertThat(notDeletedProduct.isPresent()).isTrue();

        assertThat(product.isEnabled()).isTrue();
        mockMvc.perform(delete("/product/{id}", product.getId()))
                .andExpect(status().isOk());

        var deletedProduct = productRepository.findById(product.getId());
        assertThat(deletedProduct.isPresent()).isFalse();
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenCreate_thenShouldReturnCreated() throws Exception {

        var productSave = ProductSaveVO.builder()
                .sku("sku")
                .name("name")
                .description("description")
                .price(1D)
                .inventory(2)
                .shipmentDeliveryTimes(3)
                .enabled(true)
                .categoryId(1L)
                .build();

        mockMvc.perform(post("/product/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSave)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("http://*/product/sku"));

        var product = productRepository.findById(1L).get();

        assertThat(product.getSku()).isEqualTo(productSave.getSku());
        assertThat(product.getName()).isEqualTo(productSave.getName());
        assertThat(product.getDescription()).isEqualTo(productSave.getDescription());
        assertThat(product.getPrice()).isEqualTo(productSave.getPrice());
        assertThat(product.getInventory()).isEqualTo(productSave.getInventory());
        assertThat(product.getShipmentDeliveryTimes()).isEqualTo(productSave.getShipmentDeliveryTimes());
        assertThat(product.isEnabled()).isEqualTo(productSave.isEnabled());
        assertThat(product.getCategory().getId()).isEqualTo(productSave.getCategoryId());
        assertThat(product.getCreationDate()).isNotNull();
        assertThat(product.getImage()).isNull();
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenCreateWithImage_thenShouldReturnCreatedAndCreateImage() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("test.jpg")) {
            var bytes = in.readAllBytes();
            var imageBase64 = "data:image/jpg;base64," + Base64.getEncoder().encodeToString(bytes);
            var productSave = ProductSaveVO.builder()
                    .sku("sku")
                    .name("name")
                    .description("description")
                    .price(1D)
                    .inventory(2)
                    .shipmentDeliveryTimes(3)
                    .enabled(true)
                    .image(imageBase64)
                    .categoryId(1L)
                    .build();

            mockMvc.perform(post("/product/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSave)))
                    .andExpect(status().isCreated())
                    .andExpect(redirectedUrlPattern("http://*/product/sku"));

            var product = productRepository.findById(1L).get();

            assertThat(product.getSku()).isEqualTo(productSave.getSku());
            assertThat(product.getName()).isEqualTo(productSave.getName());
            assertThat(product.getDescription()).isEqualTo(productSave.getDescription());
            assertThat(product.getPrice()).isEqualTo(productSave.getPrice());
            assertThat(product.getInventory()).isEqualTo(productSave.getInventory());
            assertThat(product.getShipmentDeliveryTimes()).isEqualTo(productSave.getShipmentDeliveryTimes());
            assertThat(product.isEnabled()).isEqualTo(productSave.isEnabled());
            assertThat(product.getCategory().getId()).isEqualTo(productSave.getCategoryId());
            assertThat(product.getCreationDate()).isNotNull();
            assertThat(product.getImage()).isNotNull();

            var s3Object = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(awsProperties.getBucket())
                    .key(product.getSku())
                    .build());
            assertThat(s3Object.response().contentLength()).isGreaterThan(0);

        }
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenUpdate_thenShouldReturnOk() throws Exception {

        var product = createProduct();
        var newCategory = createProductCategory("category 2");

        var productFound = productRepository.findById(product.getId()).get();

        assertThat(productFound.getSku()).isEqualTo(product.getSku());
        assertThat(productFound.getName()).isEqualTo(product.getName());
        assertThat(productFound.getDescription()).isEqualTo(product.getDescription());
        assertThat(productFound.getPrice()).isEqualTo(product.getPrice());
        assertThat(productFound.getInventory()).isEqualTo(product.getInventory());
        assertThat(productFound.getShipmentDeliveryTimes()).isEqualTo(product.getShipmentDeliveryTimes());
        assertThat(productFound.isEnabled()).isEqualTo(product.isEnabled());
        assertThat(productFound.getCategory().getId()).isEqualTo(product.getCategory().getId());
        assertThat(productFound.getCreationDate()).isNotNull();

        var productSave = ProductSaveVO.builder()
                .sku("newsku")
                .name("new name")
                .description("new description")
                .price(10D)
                .inventory(20)
                .shipmentDeliveryTimes(30)
                .enabled(false)
                .categoryId(newCategory.getId())
                .build();

        mockMvc.perform(put("/product/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSave)))
                .andExpect(status().isOk());

        productFound = productRepository.findById(product.getId()).get();

        assertThat(productFound.getSku()).isEqualTo(productSave.getSku());
        assertThat(productFound.getName()).isEqualTo(productSave.getName());
        assertThat(productFound.getDescription()).isEqualTo(productSave.getDescription());
        assertThat(productFound.getPrice()).isEqualTo(productSave.getPrice());
        assertThat(productFound.getInventory()).isEqualTo(productSave.getInventory());
        assertThat(productFound.getShipmentDeliveryTimes()).isEqualTo(productSave.getShipmentDeliveryTimes());
        assertThat(productFound.isEnabled()).isEqualTo(productSave.isEnabled());
        assertThat(productFound.getCategory().getId()).isEqualTo(productSave.getCategoryId());
        assertThat(productFound.getCreationDate()).isNotNull();
    }

    @Test
    @WithMockJwtAuth({"ROLE_admin"})
    public void whenUpdateWithInvalidId_thenShouldReturnNotFound() throws Exception {

        var productSave = ProductSaveVO.builder()
                .sku("sku")
                .name("name")
                .description("description")
                .price(1D)
                .inventory(2)
                .shipmentDeliveryTimes(3)
                .enabled(true)
                .categoryId(1L)
                .build();

        mockMvc.perform(put("/product/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productSave)))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Not found."));
    }

    @Test
    @WithMockJwtAuth(authorities = {"ROLE_customer"}, claims = @OpenIdClaims(email = "user1@so5.com"))
    public void whenPurchase_thenShouldReturnOk() throws Exception {

        createProduct();
        createProduct(2, true, 1L);

        var address = createAddress();

        var customer = createCustomer(address);

        createCreditCardData(customer);

        mockMvc.perform(post("/product/purchase").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(Set.of("sku1", "sku2"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuth(authorities = {"ROLE_customer"}, claims = @OpenIdClaims(email = "user1@so5.com"))
    public void whenPurchaseWithNonExistingEmail_thenShouldReturnNotFound() throws Exception {

        createProduct();
        createProduct(2, true, 1L);

        mockMvc.perform(post("/product/purchase").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(Set.of("sku1", "sku2"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockJwtAuth(authorities = {"ROLE_customer"}, claims = @OpenIdClaims(email = "user1@so5.com"))
    public void whenPurchaseWithNoCreditCardData_thenShouldReturnInternalServerError() throws Exception {

        createProduct();
        createProduct(2, true, 1L);

        var address = createAddress();

        createCustomer(address);

        mockMvc.perform(post("/product/purchase").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(Set.of("sku1", "sku2"))))
                .andExpect(status().isInternalServerError());
    }

    private void createCreditCardData(Customer customer) {
        creditCardDataRepository.save(CreditCardData.builder()
                .creationDate(LocalDateTime.now())
                .customer(customer)
                .expirationDate(LocalDate.now())
                .holder("holder")
                .number("number")
                .verificationCode("123")
                .build());
    }

    private Customer createCustomer(Address address) {
        return customerRepository.save(Customer.builder()
                .email("user1@so5.com")
                .billingAddress(address)
                .shippingAddress(address)
                .build()
        );
    }

    private Address createAddress() {
        return addressRepository.save(Address.builder()
                .city("city")
                .country("country")
                .creationDate(LocalDateTime.now())
                .district("district")
                .number("number")
                .state("state")
                .street("street")
                .zipCode("zipCode")
                .build());
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
