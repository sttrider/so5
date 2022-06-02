package com.so5.api.controller;

import com.so5.api.entity.Product;
import com.so5.api.service.CustomerService;
import com.so5.api.service.ProductService;
import com.so5.api.vo.ProductSaveVO;
import com.so5.api.vo.SearchProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> create(@RequestBody ProductSaveVO productSaveVO, UriComponentsBuilder uriComponentsBuilder) {

        var product = productService.save(productSaveVO);

        var uriComponents =
                uriComponentsBuilder.path("/product/{id}").buildAndExpand(product.getSku());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PostMapping("/list")
    @Secured({"ROLE_admin"})
    public List<Product> list(@RequestBody SearchProductVO searchProductVO) {

        return productService.list(searchProductVO);
    }

    @PutMapping("/{sku}/{enabled}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_admin"})
    public void changeStatus(@PathVariable String sku, @PathVariable boolean enabled) {
        productService.changeStatus(sku, enabled);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_admin"})
    public void update(@RequestBody ProductSaveVO productSaveVO, @PathVariable Long id) {

        productService.save(productSaveVO, id);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_admin"})
    public void delete(@PathVariable Long id) {
        productService.delete(id);
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
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_customer"})
    public void purchase(@RequestBody Set<String> purchaseVO, JwtAuthenticationToken principal) {
        log.info("Purchase {}", purchaseVO);
        productService.purchase(purchaseVO, customerService.findByEmail(principal.getTokenAttributes().get("email").toString()));
    }
}
