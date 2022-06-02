package com.so5.api.service;

import com.so5.api.entity.Customer;
import com.so5.api.entity.Product;
import com.so5.api.entity.ProductCategory;
import com.so5.api.exception.EntityNotFoundException;
import com.so5.api.exception.NoCreditCardDataException;
import com.so5.api.exception.ResizeImageException;
import com.so5.api.repository.ProductRepository;
import com.so5.api.vo.ProductSaveVO;
import com.so5.api.vo.SearchProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CreditCardDataService creditCardDataService;
    private final ImageResizeService imageResizeService;
    private final S3Service s3Service;

    public Product save(ProductSaveVO productSaveVO) {

        return save(productSaveVO, null);
    }

    public Product save(ProductSaveVO productSaveVO, Long id) {

        Product product;
        if (id != null) {
            product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        } else {
            product = new Product();
            product.setCreationDate(LocalDateTime.now());
        }

        var image = uploadImage(productSaveVO);

        product.setSku(productSaveVO.getSku());
        product.setName(productSaveVO.getName());
        product.setDescription(productSaveVO.getDescription());
        product.setPrice(productSaveVO.getPrice());
        product.setInventory(productSaveVO.getInventory());
        product.setShipmentDeliveryTimes(productSaveVO.getShipmentDeliveryTimes());
        product.setEnabled(productSaveVO.isEnabled());
        if (image != null) {
            product.setImage(image);
        }
        product.setCategory(ProductCategory.builder()
                .id(productSaveVO.getCategoryId())
                .build());

        return productRepository.save(product);
    }


    public Product findBySku(String sku) {
        return productRepository.findBySku(sku).orElseThrow(EntityNotFoundException::new);
    }

    public List<Product> search(SearchProductVO searchProductVO) {

        return productRepository.findByCategoryAndEnabled(ProductCategory.builder()
                .id(searchProductVO.getCategoryId())
                .build(), true, PageRequest.of(0, 20));
    }

    public List<Product> list(SearchProductVO searchProductVO) {

        return productRepository.findByCategory(ProductCategory.builder()
                .id(searchProductVO.getCategoryId())
                .build(), PageRequest.of(0, 20));
    }

    public void purchase(Set<String> purchaseVO, Customer customer) {
        log.info("Purchase {} - {}", purchaseVO, customer);

        creditCardDataService.findByCustomer(customer).orElseThrow(NoCreditCardDataException::new);
    }

    public void changeStatus(String sku, boolean enabled) {
        productRepository.updateStatusBySku(enabled, sku);
    }

    public void delete(Long id) {

        var optionalProduct = productRepository.findById(id);

        optionalProduct.ifPresent((product -> {

            s3Service.delete(product.getSku());
            productRepository.deleteById(product.getId());
        }));

    }

    private String uploadImage(ProductSaveVO productSaveVO) {
        if (productSaveVO.getImage() == null) {
            return null;
        }
        var base62Splitted = productSaveVO.getImage().split(",");
        var image = Base64.getDecoder().decode(base62Splitted[1]);
        InputStream fis = new ByteArrayInputStream(image);

        String extension = StringUtils.substringBetween(base62Splitted[0], "/", ";");
        String contentType = StringUtils.substringBetween(base62Splitted[0], ":", ";");

        try {
            ByteArrayOutputStream os = imageResizeService.resizeImage(fis, extension);
            return s3Service.upload(os, productSaveVO.getSku(), contentType);
        } catch (ResizeImageException e) {
            log.error("Error resizing image.", e);
            return null;
        }
    }
}
