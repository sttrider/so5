package com.so5.api.service;

import com.so5.api.entity.Customer;
import com.so5.api.entity.Product;
import com.so5.api.entity.ProductCategory;
import com.so5.api.exception.EntityNotFoundException;
import com.so5.api.exception.NoCreditCardDataException;
import com.so5.api.repository.ProductRepository;
import com.so5.api.vo.ProductCreateVO;
import com.so5.api.vo.SearchProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public Product create(ProductCreateVO productCreateVO) {

        var image = uploadImage(productCreateVO);

        var product = Product.builder()
                .sku(productCreateVO.getSku())
                .name(productCreateVO.getName())
                .description(productCreateVO.getDescription())
                .price(productCreateVO.getPrice())
                .inventory(productCreateVO.getInventory())
                .shipmentDeliveryTimes(productCreateVO.getShipmentDeliveryTimes())
                .enabled(productCreateVO.isEnabled())
                .image(image)
                .category(ProductCategory.builder()
                        .id(productCreateVO.getCategoryId())
                        .build())
                .creationDate(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }


    public Product findBySku(String sku) {
        return productRepository.findBySku(sku).orElseThrow(EntityNotFoundException::new);
    }

    public List<Product> search(SearchProductVO searchProductVO) {

        return productRepository.findByCategory(ProductCategory.builder().id(searchProductVO.getCategoryId()).build(), PageRequest.of(0, 20));
    }

    public void purchase(Set<String> purchaseVO, Customer customer) {
        log.info("Purchase {} - {}", purchaseVO, customer);

        creditCardDataService.findByCustomer(customer).orElseThrow(NoCreditCardDataException::new);
    }

    private String uploadImage(ProductCreateVO productCreateVO) {
        if (productCreateVO.getImage() == null) {
            return null;
        }
        var base62Splitted = productCreateVO.getImage().split(",");
        var image = Base64.getDecoder().decode(base62Splitted[1]);
        InputStream fis = new ByteArrayInputStream(image);

        String extension = StringUtils.substringBetween(base62Splitted[0], "/", ";");
        String contentType = StringUtils.substringBetween(base62Splitted[0], ":", ";");

        try {
            ByteArrayOutputStream os = imageResizeService.resizeImage(fis, extension);
            return s3Service.upload(os, productCreateVO.getSku(), contentType);
        } catch (IOException e) {
            log.error("Error resizing image.", e);
            return null;
        }
    }
}
