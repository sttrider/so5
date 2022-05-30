package com.so5.api.controller;

import com.so5.api.entity.Product;
import com.so5.api.service.CustomerService;
import com.so5.api.service.ProductService;
import com.so5.api.vo.ProductCreateVO;
import com.so5.api.vo.SearchProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final CustomerService customerService;

    @PostMapping("/")
    @Secured({"ROLE_admin"})
    public ResponseEntity<Void> create(@RequestBody ProductCreateVO productCreateVO, UriComponentsBuilder uriComponentsBuilder) {

        var product = productService.create(productCreateVO);

        var uriComponents =
                uriComponentsBuilder.path("/product/{id}").buildAndExpand(product.getSku());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @GetMapping("/{sku}")
    public Product findBySku(@PathVariable String sku) {

        return productService.findBySku(sku);
    }

    @PostMapping("/search")
    public List<Product> search(@RequestBody SearchProductVO searchProductVO) {

        return productService.search(searchProductVO);
    }

    @PostMapping("/purchase")
    @Secured({"ROLE_customer"})
    public void purchase(@RequestBody Set<String> purchaseVO, JwtAuthenticationToken principal) {
        log.info("Purchase {}", purchaseVO);
        productService.purchase(purchaseVO, customerService.findByEmail(principal.getTokenAttributes().get("email").toString()));
    }
}
