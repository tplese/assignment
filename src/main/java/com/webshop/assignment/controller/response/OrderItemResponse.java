package com.webshop.assignment.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {

    private String productId;
    private String productName;
    private BigDecimal productPriceHrk;
    private Long quantity;
    private BigDecimal totalItemPriceHrk;
}
