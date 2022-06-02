package com.so5.api.service;

import com.so5.api.entity.CreditCardData;
import com.so5.api.entity.Customer;
import com.so5.api.entity.Product;
import com.so5.api.entity.ProductCategory;
import com.so5.api.exception.EntityNotFoundException;
import com.so5.api.exception.NoCreditCardDataException;
import com.so5.api.exception.ResizeImageException;
import com.so5.api.repository.ProductRepository;
import com.so5.api.vo.ProductSaveVO;
import com.so5.api.vo.SearchProductVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CreditCardDataService creditCardDataService;

    @Mock
    private ImageResizeService imageResizeService;

    @Mock
    private S3Service s3Service;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    @Captor
    ArgumentCaptor<ProductCategory> productCategoryCaptor;

    @InjectMocks
    private ProductService productService;

    @Test
    public void whenSaveNewProduct_shouldNotCallFindById() throws ResizeImageException {

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

        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        productService.save(productSave);

        verify(productRepository, never()).findById(anyLong());
        verify(imageResizeService, never()).resizeImage(any(), anyString());
        verify(s3Service, never()).upload(any(), anyString(), anyString());
        verify(productRepository).save(productCaptor.capture());
        var product = productCaptor.getValue();

        assertThat(product.getSku()).isEqualTo(productSave.getSku());
        assertThat(product.getName()).isEqualTo(productSave.getName());
        assertThat(product.getDescription()).isEqualTo(productSave.getDescription());
        assertThat(product.getPrice()).isEqualTo(productSave.getPrice());
        assertThat(product.getInventory()).isEqualTo(productSave.getInventory());
        assertThat(product.getShipmentDeliveryTimes()).isEqualTo(productSave.getShipmentDeliveryTimes());
        assertThat(product.isEnabled()).isEqualTo(productSave.isEnabled());
        assertThat(product.getCategory().getId()).isEqualTo(productSave.getCategoryId());
        assertThat(product.getCreationDate()).isNotNull();
    }

    @Test
    public void whenSaveOldProduct_shouldCallFindById() throws ResizeImageException {

        var id = 1L;
        var productSave = ProductSaveVO.builder()
                .sku("sku 2")
                .name("name 2")
                .description("description 2")
                .price(10D)
                .inventory(20)
                .shipmentDeliveryTimes(30)
                .enabled(false)
                .categoryId(40L)
                .build();

        var product = Product.builder()
                .id(id)
                .sku("sku")
                .name("name")
                .description("description")
                .price(1D)
                .inventory(2)
                .shipmentDeliveryTimes(3)
                .enabled(true)
                .category(ProductCategory.builder()
                        .id(4L)
                        .build())
                .creationDate(LocalDateTime.now())
                .build();

        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.save(productSave, id);

        verify(productRepository).findById(id);
        verify(imageResizeService, never()).resizeImage(any(), anyString());
        verify(s3Service, never()).upload(any(), anyString(), anyString());
        verify(productRepository).save(productCaptor.capture());
        var productCaptured = productCaptor.getValue();

        assertThat(productCaptured.getId()).isEqualTo(id);
        assertThat(productCaptured.getSku()).isEqualTo(productSave.getSku());
        assertThat(productCaptured.getName()).isEqualTo(productSave.getName());
        assertThat(productCaptured.getDescription()).isEqualTo(productSave.getDescription());
        assertThat(productCaptured.getPrice()).isEqualTo(productSave.getPrice());
        assertThat(productCaptured.getInventory()).isEqualTo(productSave.getInventory());
        assertThat(productCaptured.getShipmentDeliveryTimes()).isEqualTo(productSave.getShipmentDeliveryTimes());
        assertThat(productCaptured.isEnabled()).isEqualTo(productSave.isEnabled());
        assertThat(productCaptured.getCategory().getId()).isEqualTo(productSave.getCategoryId());
        assertThat(productCaptured.getCreationDate()).isNotNull();
    }

    @Test
    public void whenSaveWithNonExistingId_thenShouldThrowEntityNotFoundException() throws ResizeImageException {

        var id = 1L;
        var productSave = ProductSaveVO.builder()
                .sku("sku 2")
                .name("name 2")
                .description("description 2")
                .price(10D)
                .inventory(20)
                .shipmentDeliveryTimes(30)
                .enabled(false)
                .categoryId(40L)
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> productService.save(productSave, id));

        assertThat(exception.getReason()).isEqualTo("Not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository).findById(id);
        verify(imageResizeService, never()).resizeImage(any(), anyString());
        verify(s3Service, never()).upload(any(), anyString(), anyString());
        verify(productRepository, never()).save(any());

    }

    @Test
    public void whenSaveWithImage_shouldResizeAndSendToS3() throws ResizeImageException, IOException {

        File file = new File(getClass().getClassLoader().getResource("test.jpg").getFile());
        try (InputStream in = new FileInputStream(file)) {
            var imageBase64 = "data:image/jpg;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());

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

            when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

            when(s3Service.upload(any(), eq("sku"), eq("image/jpg"))).thenReturn("http://localhost:4566/so5/sku");

            productService.save(productSave);

            verify(productRepository, never()).findById(anyLong());
            verify(imageResizeService).resizeImage(any(), eq("jpg"));
            verify(s3Service).upload(any(), eq("sku"), eq("image/jpg"));
            verify(productRepository).save(productCaptor.capture());
            var product = productCaptor.getValue();

            assertThat(product.getSku()).isEqualTo(productSave.getSku());
            assertThat(product.getName()).isEqualTo(productSave.getName());
            assertThat(product.getDescription()).isEqualTo(productSave.getDescription());
            assertThat(product.getPrice()).isEqualTo(productSave.getPrice());
            assertThat(product.getInventory()).isEqualTo(productSave.getInventory());
            assertThat(product.getShipmentDeliveryTimes()).isEqualTo(productSave.getShipmentDeliveryTimes());
            assertThat(product.isEnabled()).isEqualTo(productSave.isEnabled());
            assertThat(product.getCategory().getId()).isEqualTo(productSave.getCategoryId());
            assertThat(product.getCreationDate()).isNotNull();
            assertThat(product.getImage()).isEqualTo("http://localhost:4566/so5/sku");
        }

    }

    @Test
    public void whenSaveWithResizeError_shouldReturnNullAndNotSendToS3() throws ResizeImageException, IOException {
        File file = new File(getClass().getClassLoader().getResource("test.jpg").getFile());
        try (InputStream in = new FileInputStream(file)) {
            var imageBase64 = "data:image/jpg;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());

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

            when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
            when(imageResizeService.resizeImage(any(), eq("jpg"))).thenThrow(new ResizeImageException());

            productService.save(productSave);

            verify(productRepository, never()).findById(anyLong());
            verify(imageResizeService).resizeImage(any(), eq("jpg"));
            verify(s3Service, never()).upload(any(), anyString(), anyString());
            verify(productRepository).save(productCaptor.capture());
            var product = productCaptor.getValue();

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
    }

    @Test
    public void whenFindBySku_thenShouldCallRepository() {

        var sku = "sku";

        when(productRepository.findBySku(sku)).thenReturn(Optional.of(new Product()));

        productService.findBySku(sku);

        verify(productRepository).findBySku(sku);
    }

    @Test
    public void whenFindBySkuWithNonExistingSku_thenShouldThrowEntityNotFoundException() {

        var sku = "sku";

        when(productRepository.findBySku(sku)).thenReturn(Optional.empty());

        var exception = assertThrows(EntityNotFoundException.class, () -> productService.findBySku(sku));
        assertThat(exception.getReason()).isEqualTo("Not found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository).findBySku(sku);
    }

    @Test
    public void whenSearch_thenShouldCallRepository() {

        var searchProductVO = SearchProductVO.builder().categoryId(1L).build();
        productService.search(searchProductVO);

        verify(productRepository).findByCategoryAndEnabled(productCategoryCaptor.capture(), eq(true), eq(PageRequest.of(0, 20)));

        var capturedValue = productCategoryCaptor.getValue();

        assertThat(capturedValue.getId()).isEqualTo(searchProductVO.getCategoryId());
    }

    @Test
    public void whenList_thenShouldCallRepository() {

        var searchProductVO = SearchProductVO.builder().categoryId(1L).build();
        productService.list(searchProductVO);

        verify(productRepository).findByCategory(productCategoryCaptor.capture(), eq(PageRequest.of(0, 20)));

        var capturedValue = productCategoryCaptor.getValue();

        assertThat(capturedValue.getId()).isEqualTo(searchProductVO.getCategoryId());
    }

    @Test
    public void whenPurchase_thenShouldCallCreditCardService() {

        var customer = new Customer();

        when(creditCardDataService.findByCustomer(customer)).thenReturn(Optional.of(new CreditCardData()));

        productService.purchase(Set.of("sku"), customer);

        verify(creditCardDataService).findByCustomer(customer);
    }

    @Test
    public void whenPurchaseWithoutCreditCardData_thenShouldThrowNoCreditCardDataException() {

        var customer = new Customer();

        when(creditCardDataService.findByCustomer(customer)).thenReturn(Optional.empty());

        var exception = assertThrows(NoCreditCardDataException.class, () -> productService.purchase(Set.of("sku"), customer));
        assertThat(exception.getReason()).isEqualTo("No credit card data found.");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        verify(creditCardDataService).findByCustomer(customer);
    }

    @Test
    public void whenChangeStatus_thenShouldCallRepository() {

        productService.changeStatus("sku", true);

        verify(productRepository).updateStatusBySku(true, "sku");
    }

    @Test
    public void whenDelete_thenShouldCallRepository() {

        var id = 1L;
        var sku = "sku";

        when(productRepository.findById(id)).thenReturn(Optional.of(Product.builder()
                .sku(sku)
                .id(id)
                .build()));
        productService.delete(id);

        verify(productRepository).deleteById(id);
        verify(s3Service).delete(sku);
    }

    @Test
    public void whenDeleteWithNonExistingId_thenShouldDoNothing() {

        var id = 1L;
        var sku = "sku";

        when(productRepository.findById(id)).thenReturn(Optional.empty());
        productService.delete(id);

        verify(productRepository, never()).deleteById(id);
        verify(s3Service, never()).delete(sku);
    }
}
