package com.so5.api.controller;

import com.so5.api.entity.Product;
import com.so5.api.service.CustomerService;
import com.so5.api.service.ProductService;
import com.so5.api.vo.ProductSaveVO;
import com.so5.api.vo.SearchProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created")
    })
    @PostMapping("/")
    @Secured({"ROLE_admin"})
    public ResponseEntity<Void> create(@RequestBody ProductSaveVO productSaveVO, UriComponentsBuilder uriComponentsBuilder) {

        var product = productService.save(productSaveVO);

        var uriComponents =
                uriComponentsBuilder.path("/product/{id}").buildAndExpand(product.getSku());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @Operation(summary = "Find a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))
            })
    })
    @PostMapping("/list")
    @Secured({"ROLE_admin"})
    public List<Product> list(@RequestBody SearchProductVO searchProductVO) {

        return productService.list(searchProductVO);
    }

    @Operation(summary = "Update a product status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product status updated")
    })
    @PutMapping("/{sku}/{enabled}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_admin"})
    public void changeStatus(@PathVariable String sku, @PathVariable boolean enabled) {
        productService.changeStatus(sku, enabled);
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_admin"})
    public void update(@RequestBody ProductSaveVO productSaveVO, @PathVariable Long id) {

        productService.save(productSaveVO, id);

    }

    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_admin"})
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @Operation(summary = "Get a product by sku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{sku}")
    public Product findBySku(@PathVariable String sku) {

        return productService.findBySku(sku);
    }

    @Operation(summary = "Find a list of all active products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))
            })
    })
    @PostMapping("/search")
    public List<Product> search(@RequestBody SearchProductVO searchProductVO) {

        return productService.search(searchProductVO);
    }

    @Operation(summary = "Do the purchase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchased"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "User credit card not found")
    })
    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_customer"})
    public void purchase(@RequestBody Set<String> purchaseVO, JwtAuthenticationToken principal) {
        log.info("Purchase {}", purchaseVO);
        productService.purchase(purchaseVO, customerService.findByEmail(principal.getTokenAttributes().get("email").toString()));
    }
}
