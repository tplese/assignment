package com.webshop.assignment.controller;

import com.webshop.assignment.controller.request.OrderRequest;
import com.webshop.assignment.controller.response.OrderResponse;
import com.webshop.assignment.service.OrderResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/")
@RequiredArgsConstructor
public class OrderController {

    private final OrderResponseService orderResponseService;

    @GetMapping(value = "/read-order/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderResponse getOrder(@PathVariable String orderId) {
        return orderResponseService.getOrder(orderId);
    }

    @PostMapping(value = "submit-order/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderResponse submitOrder(@PathVariable String orderId) {
        return orderResponseService.submitOrder(orderId);
    }

    @PostMapping(value = "/create-order")
    public OrderResponse postOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderResponseService.postOrder(orderRequest);
    }

    @PutMapping(value = "/update-order/{orderId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderResponse putOrder(@PathVariable String orderId, @Valid @RequestBody OrderRequest orderRequest) {
        return orderResponseService.putOrder(orderId, orderRequest);
    }

    @DeleteMapping(value = "/delete-order/{orderId}")
    public void deleteOrder(@PathVariable String orderId) {
        orderResponseService.deleteOrder(orderId);
    }
}
