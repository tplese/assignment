package com.webshop.assignment.controller.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrderItemRequest {

    @NotBlank
    private String productId;

    @NotNull
    @Min(0)
    private Long quantity;
}
