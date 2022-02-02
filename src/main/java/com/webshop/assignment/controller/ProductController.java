package com.webshop.assignment.controller;

import com.webshop.assignment.controller.request.ProductRequest;
import com.webshop.assignment.controller.response.ProductResponse;
import com.webshop.assignment.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getProduct(@PathVariable String productId) {
        return productService.getProduct(productId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse postProduct(@Valid @RequestBody ProductRequest productRequest) {
        return productService.postProduct(productRequest);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse putProduct(@PathVariable String productId,
                                      @Valid @RequestBody ProductRequest productRequest) {
        return productService.putProduct(productId, productRequest);
    }

    @DeleteMapping(value = "/{productId}")
    public void deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
    }
}
