package com.webshop.assignment.controller;

import com.webshop.assignment.controller.request.CustomerRequest;
import com.webshop.assignment.controller.response.CustomerResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.repository.CustomerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    private final Customer newCustomer = new Customer();

    @BeforeEach
    public void beforeEach() {
        customerRepository.deleteAll();

        newCustomer.setFirstName("Leo");
        newCustomer.setLastName("Leonid");
        newCustomer.setEmail("lleonid@gmail.com");
    }

    @Test
    void getCustomer_ValidCustomerId_CustomerReturned() {
        // arrange
        Customer customerSavedToDb = customerRepository.save(newCustomer);

        // act
        String url = "http://localhost:" + port + "/api/v1/customer/" + customerSavedToDb.getId();
        ResponseEntity<CustomerResponse> customerResponse =
                testRestTemplate.getForEntity(url, CustomerResponse.class);
        CustomerResponse customerResponseBody = customerResponse.getBody();

        // assert
        assert customerResponseBody != null;
        assertEquals(newCustomer.getFirstName(), customerResponseBody.getFirstName());
        assertEquals(newCustomer.getLastName(), customerResponseBody.getLastName());
        assertEquals(newCustomer.getEmail(), customerResponseBody.getEmail());
    }

    @Test
    void postCustomer_ValidCustomerRequest_CustomerCreated() {
        // arrange
        HttpEntity<Customer> request = new HttpEntity<>(newCustomer);

        // act
        String url = "http://localhost:" + port + "/api/v1/customer/";
        ResponseEntity<CustomerResponse> customerResponse =
                testRestTemplate.exchange(url, HttpMethod.POST, request, CustomerResponse.class);
        CustomerResponse customerResponseBody = customerResponse.getBody();

        // assert
        assert customerResponseBody != null;
        assertEquals(newCustomer.getFirstName(), customerResponseBody.getFirstName());
        assertEquals(newCustomer.getLastName(), customerResponseBody.getLastName());
        assertEquals(newCustomer.getEmail(), customerResponseBody.getEmail());
    }

    @Test
    void putCustomer_ValidCustomerRequestAndCustomerId_CustomerUpdated() {
        // arrange
        Customer customerSavedToDb = customerRepository.save(newCustomer);

        CustomerRequest changedCustomer = new CustomerRequest();
        changedCustomer.setFirstName("Emil");
        changedCustomer.setLastName("Emilion");
        changedCustomer.setEmail("eemilion@gmail.com");

        HttpEntity<CustomerRequest> request = new HttpEntity<>(changedCustomer);

        // act
        String url = "http://localhost:" + port + "/api/v1/customer/" + customerSavedToDb.getId().toString();
        ResponseEntity<CustomerResponse> customerResponse =
                testRestTemplate.exchange(url, HttpMethod.PUT, request, CustomerResponse.class);
        CustomerResponse customerResponseBody = customerResponse.getBody();

        // assert
        assert customerResponseBody != null;
        assertEquals(customerSavedToDb.getId().toString(), customerResponseBody.getId());
        assertEquals(changedCustomer.getFirstName(), customerResponseBody.getFirstName());
        assertEquals(changedCustomer.getLastName(), customerResponseBody.getLastName());
        assertEquals(changedCustomer.getEmail(), customerResponseBody.getEmail());
    }

    @Test
    void deleteCustomer_ValidCustomerId_CustomerDeleted() {
        // arrange
        Customer customerSavedToDb = customerRepository.save(newCustomer);

        // act
        String url = "http://localhost:" + port + "/api/v1/customer/" + customerSavedToDb.getId();
        testRestTemplate.delete(url);

        // assert
        assertTrue(customerRepository.findById(customerSavedToDb.getId()).isEmpty());
    }

    @AfterAll
    public static void afterAll(@Autowired CustomerRepository customerRepository) {
        customerRepository.deleteAll();
    }
}
