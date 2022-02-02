package com.webshop.assignment.controller;

import com.webshop.assignment.controller.request.CustomerRequest;
import com.webshop.assignment.controller.response.CustomerResponse;
import com.webshop.assignment.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponse getCustomer(@PathVariable String customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponse postCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return customerService.postCustomer(customerRequest);
    }

    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponse putCustomer(@PathVariable String customerId,
                                        @Valid @RequestBody CustomerRequest customerRequest) {
        return customerService.putCustomer(customerId ,customerRequest);
    }

    @DeleteMapping(value = "/{customerId}")
    public void deleteCustomer(@PathVariable String customerId) {
        customerService.deleteCustomer(customerId);
    }
}
