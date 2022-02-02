package com.webshop.assignment.controller;

import com.webshop.assignment.controller.response.ProductResponse;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
public class ProductControllerMvcTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    private final String validProductRequest =
            "{" +
                    "\"code\": \"1234567890\", " +
                    "\"name\" : \"Hammer\", " +
                    "\"description\" : \"5 kg\", " +
                    "\"priceHrk\" : 5.00, " +
                    "\"isAvailable\" : \"true\"" +
                    "}";

    private final String invalidProductRequest =
            "{" +
                    "\"code\": \"\", " +
                    "\"name\" : \"\", " +
                    "\"description\" : \"\", " +
                    "\"priceHrk\" : , " +
                    "\"isAvailable\" : \"\"" +
                    "}";

    @Test
    public void getProduct_InvalidProductId_NotFound() throws Exception {
        String invalidProductId = UUID.randomUUID().toString();
        Mockito.when(productService.getProduct(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/product/" + invalidProductId, 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProduct_ValidProductId_StatusOk() throws Exception {
        ProductResponse validProductResponse = createValidProductResponse();
        Mockito.when(productService.getProduct(any())).thenReturn(validProductResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/product/" + validProductResponse.getId(), 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void postProduct_InvalidProductRequest_BadRequest() throws Exception {
        ProductResponse invalidProductResponse = createInvalidProductResponse();
        Mockito.when(productService.postProduct(any())).thenReturn(invalidProductResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/product", 1)
                        .content(invalidProductRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postProduct_ValidProductRequest_StatusOk() throws Exception {
        ProductResponse validProductResponse = createValidProductResponse();
        Mockito.when(productService.postProduct(any())).thenReturn(validProductResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/product", 1)
                        .content(validProductRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void putProduct_InvalidProductRequest_BadRequest() throws Exception {
        ProductResponse invalidProductResponse = createInvalidProductResponse();
        Mockito.when(productService.putProduct(any(), any())).thenReturn(invalidProductResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/product/" + invalidProductResponse.getId(), 2)
                        .content(invalidProductRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putProduct_ValidProductRequest_StatusOk() throws Exception {
        ProductResponse validProductResponse = createValidProductResponse();
        Mockito.when(productService.putProduct(any(), any())).thenReturn(validProductResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/product/" + validProductResponse.getId(), 2)
                        .content(validProductRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void deleteProduct_ExistingProduct_StatusOk() throws Exception {
        String invalidProductId = UUID.randomUUID().toString();
        Mockito.doNothing().when(productService).deleteProduct(any());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/product/" + invalidProductId, 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private Product createValidProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setCode("1234567890");
        product.setName("Hammer");
        product.setDescription("10 kg");
        product.setPriceHrk(BigDecimal.valueOf(50.00));
        product.setIsAvailable(true);

        return product;
    }

    private ProductResponse createValidProductResponse() {
        Product product = createValidProduct();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId().toString());
        productResponse.setCode(product.getCode());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPriceHrk(product.getPriceHrk());
        productResponse.setIsAvailable(product.getIsAvailable());

        return productResponse;
    }

    private Product createInvalidProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setCode("");
        product.setName("");
        product.setDescription("");
        product.setPriceHrk(BigDecimal.valueOf(-1.00));
        product.setIsAvailable(true);

        return product;
    }

    private ProductResponse createInvalidProductResponse() {
        Product product = createInvalidProduct();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId().toString());
        productResponse.setCode(product.getCode());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPriceHrk(product.getPriceHrk());
        productResponse.setIsAvailable(product.getIsAvailable());

        return productResponse;
    }
}
