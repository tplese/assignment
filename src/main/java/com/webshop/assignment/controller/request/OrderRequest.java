package com.webshop.assignment.controller.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class OrderRequest {

    @NotBlank
    private String customerId;

    private List<OrderItemRequest> orderItemList;
}
