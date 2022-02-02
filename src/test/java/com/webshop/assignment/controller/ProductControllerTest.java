package com.webshop.assignment.controller;

import com.webshop.assignment.controller.request.ProductRequest;
import com.webshop.assignment.controller.response.ProductResponse;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.repository.OrderItemRepository;
import com.webshop.assignment.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private final Product newProduct = new Product();

    @BeforeEach
    public void beforeEach() {
        orderItemRepository.deleteAll();
        productRepository.deleteAll();

        newProduct.setCode("1234567890");
        newProduct.setName("Nail");
        newProduct.setPriceHrk(BigDecimal.valueOf(1.99));
        newProduct.setDescription("Big head");
        newProduct.setIsAvailable(true);
    }

    @Test
    void getProduct_ValidProductId_CustomerReturned() {
        // arrange
        Product productSavedToDb = productRepository.save(newProduct);

        // act
        String url = "http://localhost:" + port + "/api/v1/product/" + productSavedToDb.getId();
        ResponseEntity<ProductResponse> productResponse =
                testRestTemplate.getForEntity(url, ProductResponse.class);
        ProductResponse productResponseBody = productResponse.getBody();

        // assert
        assert productResponseBody != null;
        assertEquals(newProduct.getCode(), productResponseBody.getCode());
        assertEquals(newProduct.getName(), productResponseBody.getName());
        assertEquals(newProduct.getPriceHrk(), productResponseBody.getPriceHrk());
        assertEquals(newProduct.getDescription(), productResponseBody.getDescription());
        assertEquals(newProduct.getIsAvailable(), productResponseBody.getIsAvailable());
    }

    @Test
    void postProduct_ValidProductRequest_ProductCreated() {
        // arrange
        HttpEntity<Product> request = new HttpEntity<>(newProduct);

        // act
        String url = "http://localhost:" + port + "/api/v1/product/";

        ResponseEntity<ProductResponse> productResponse =
                testRestTemplate.exchange(url, HttpMethod.POST, request, ProductResponse.class);
        ProductResponse productResponseBody = productResponse.getBody();

        // assert
        assert productResponseBody != null;
        assertEquals(newProduct.getCode(), productResponseBody.getCode());
        assertEquals(newProduct.getName(), productResponseBody.getName());
        assertEquals(newProduct.getPriceHrk(), productResponseBody.getPriceHrk());
        assertEquals(newProduct.getDescription(), productResponseBody.getDescription());
        assertEquals(newProduct.getIsAvailable(), productResponseBody.getIsAvailable());
    }

    @Test
    void putProduct_ValidProductRequestAndProductId_ProductUpdated() {
        // arrange
        Product productSavedToDb = productRepository.save(newProduct);

        ProductRequest changedProduct = new ProductRequest();
        changedProduct.setCode("9876543210");
        changedProduct.setName("Screw");
        changedProduct.setPriceHrk(BigDecimal.valueOf(9.99));
        changedProduct.setDescription("Screws in easily");
        changedProduct.setIsAvailable(false);

        HttpEntity<ProductRequest> request = new HttpEntity<>(changedProduct);

        // act
        String url = "http://localhost:" + port + "/api/v1/product/" + productSavedToDb.getId();
        ResponseEntity<ProductResponse> productResponse =
                testRestTemplate.exchange(url, HttpMethod.PUT, request, ProductResponse.class);
        ProductResponse productResponseBody = productResponse.getBody();

        // assert
        assert productResponseBody != null;
        assertEquals(productSavedToDb.getId().toString(), productResponseBody.getId());
        assertEquals(changedProduct.getCode(), productResponseBody.getCode());
        assertEquals(changedProduct.getName(), productResponseBody.getName());
        assertEquals(changedProduct.getPriceHrk(), productResponseBody.getPriceHrk());
        assertEquals(changedProduct.getDescription(), productResponseBody.getDescription());
        assertEquals(changedProduct.getIsAvailable(), productResponseBody.getIsAvailable());
    }

    @Test
    void deleteProduct_ValidProductId_ProductDeleted() {
        // arrange
        Product productSavedToDb = productRepository.save(newProduct);

        // act
        String url = "http://localhost:" + port + "/api/v1/product/" + productSavedToDb.getId();
        testRestTemplate.delete(url);

        // assert
        assertTrue(productRepository.findById(productSavedToDb.getId()).isEmpty());
    }

    @AfterAll
    public static void afterAll(@Autowired ProductRepository productRepository,
                                @Autowired OrderItemRepository orderItemRepository) {
        orderItemRepository.deleteAll();
        productRepository.deleteAll();
    }
}
