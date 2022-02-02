package com.webshop.assignment.service;


import com.webshop.assignment.controller.request.ProductRequest;
import com.webshop.assignment.controller.response.ProductResponse;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse getProduct(String productId) {
        Optional<Product> product = productRepository.findById(UUID.fromString(productId));

        if (product.isPresent()) {
            return createProductResponse(product.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    public ProductResponse postProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setCode(productRequest.getCode());
        product.setName(productRequest.getName());
        product.setPriceHrk(productRequest.getPriceHrk());
        product.setDescription(productRequest.getDescription());
        product.setIsAvailable(productRequest.getIsAvailable());

        Product savedProduct = productRepository.save(product);

        return createProductResponse(savedProduct);
    }

    public ProductResponse putProduct(String productId, ProductRequest productRequest) {
        Optional<Product> product = productRepository.findById(UUID.fromString(productId));

        if (product.isPresent()) {
            if (!product.get().getCode().equals(productRequest.getCode())) {
                product.get().setCode(productRequest.getCode());
            }

            if (!product.get().getName().equals(productRequest.getName())) {
                product.get().setName(productRequest.getName());
            }

            if (!product.get().getPriceHrk().equals(productRequest.getPriceHrk())) {
                product.get().setPriceHrk(productRequest.getPriceHrk());
            }

            if (!product.get().getDescription().equals(productRequest.getDescription())) {
                product.get().setDescription(productRequest.getDescription());
            }

            if (!product.get().getIsAvailable().equals(productRequest.getIsAvailable())) {
                product.get().setIsAvailable(productRequest.getIsAvailable());
            }

            Product savedProduct = productRepository.save(product.get());

            return createProductResponse(savedProduct);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product data is malformed");
        }
    }

    public void deleteProduct(String productId) {
        Optional<Product> product = productRepository.findById(UUID.fromString(productId));

        product.ifPresent(item -> productRepository.delete(item));
    }

    public ProductResponse createProductResponse(Product savedProduct) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(savedProduct.getId().toString());
        productResponse.setCode(savedProduct.getCode());
        productResponse.setName(savedProduct.getName());
        productResponse.setPriceHrk(savedProduct.getPriceHrk());
        productResponse.setDescription(savedProduct.getDescription());
        productResponse.setIsAvailable(savedProduct.getIsAvailable());

        return productResponse;
    }
}
