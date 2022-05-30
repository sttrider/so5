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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CreditCardDataService creditCardDataService;

    public Product create(ProductCreateVO productCreateVO) {

        var product = Product.builder()
                .sku(productCreateVO.getSku())
                .name(productCreateVO.getName())
                .description(productCreateVO.getDescription())
                .price(productCreateVO.getPrice())
                .inventory(productCreateVO.getInventory())
                .shipmentDeliveryTimes(productCreateVO.getShipmentDeliveryTimes())
                .enabled(productCreateVO.isEnabled())
                .image("image")
                .category(ProductCategory.builder()
                        .id(productCreateVO.getCategoryId())
                        .build())
                .creationDate(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }

    public Product findBySku(String sku) {
        return productRepository.findById(sku).orElseThrow(EntityNotFoundException::new);
    }

    public List<Product> search(SearchProductVO searchProductVO) {

        return productRepository.findByCategory(ProductCategory.builder().id(searchProductVO.getCategoryId()).build(), PageRequest.of(0, 20));
    }

    public void purchase(Set<String> purchaseVO, Customer customer) {
        log.info("Purchase {} - {}", purchaseVO, customer);

        creditCardDataService.findByCustomer(customer).orElseThrow(NoCreditCardDataException::new);
    }
}
