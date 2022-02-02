package com.webshop.assignment.controller.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank
    @Size(min = 10, max = 10)
    private String code;

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal priceHrk;

    private String description;

    @NotNull
    private Boolean isAvailable;
}
