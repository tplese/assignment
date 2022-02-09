package com.webshop.assignment;

import com.webshop.assignment.controller.CustomerController;
import com.webshop.assignment.controller.OrderController;
import com.webshop.assignment.controller.ProductController;
import com.webshop.assignment.service.CustomerService;
import com.webshop.assignment.service.OrderResponseService;
import com.webshop.assignment.service.OrderService;
import com.webshop.assignment.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AssignmentApplicationTests {

    @Autowired
    private OrderController orderController;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private ProductController productController;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderResponseService orderResponseService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() throws Exception {
        assertThat(orderController).isNotNull();
        assertThat(customerController).isNotNull();
        assertThat(productController).isNotNull();
        assertThat(orderService).isNotNull();
        assertThat(orderResponseService).isNotNull();
        assertThat(customerService).isNotNull();
        assertThat(productService).isNotNull();
    }
}
