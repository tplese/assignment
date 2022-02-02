package com.webshop.assignment.service;

import com.webshop.assignment.controller.request.CustomerRequest;
import com.webshop.assignment.controller.response.CustomerResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse getCustomer(String customerId) {
        Optional<Customer> customer = customerRepository.findById(UUID.fromString(customerId));

        if (customer.isPresent()) {
            return createCustomerResponse(customer.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }

    public CustomerResponse postCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        customer.setEmail(customerRequest.getEmail());

        Customer savedCustomer = customerRepository.save(customer);

        return createCustomerResponse(savedCustomer);
    }

    public CustomerResponse putCustomer(String customerId, CustomerRequest customerRequest) {
        Optional<Customer> customer = customerRepository.findById(UUID.fromString(customerId));

        if (customer.isPresent()) {
            if (!customer.get().getFirstName().equals(customerRequest.getFirstName())) {
                customer.get().setFirstName(customerRequest.getFirstName());
            }

            if (!customer.get().getLastName().equals(customerRequest.getLastName())) {
                customer.get().setLastName(customerRequest.getLastName());
            }

            if (!customer.get().getEmail().equals(customerRequest.getEmail())) {
                customer.get().setEmail(customerRequest.getEmail());
            }

            Customer savedCustomer = customerRepository.save(customer.get());

            return createCustomerResponse(savedCustomer);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer data is malformed");
        }
    }

    public void deleteCustomer(String customerId) {
        Optional<Customer> customer = customerRepository.findById(UUID.fromString(customerId));

        customer.ifPresent(item -> customerRepository.delete(item));
    }

    public CustomerResponse createCustomerResponse(Customer savedCustomer) {
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(savedCustomer.getId().toString());
        customerResponse.setFirstName(savedCustomer.getFirstName());
        customerResponse.setLastName(savedCustomer.getLastName());
        customerResponse.setEmail(savedCustomer.getEmail());

        return customerResponse;
    }
}
