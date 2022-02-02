package com.webshop.assignment.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    private String id;
    private String code;
    private String name;
    private BigDecimal priceHrk;
    private String description;
    private Boolean isAvailable;
}
