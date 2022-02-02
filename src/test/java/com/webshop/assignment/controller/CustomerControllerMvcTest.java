package com.webshop.assignment.controller;

import com.webshop.assignment.controller.response.CustomerResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.service.CustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerMvcTest {

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    private final String validCustomerRequest =
            "{" +
                    "\"firstName\": \"Bob\", " +
                    "\"lastName\" : \"Bobin\", " +
                    "\"email\" : \"bbobin@gmail.com\"" +
                    "}";

    private final String invalidCustomerRequest =
            "{" +
                    "\"firstName\": \"\", " +
                    "\"lastName\" : \"\", " +
                    "\"email\" : \"\"" +
                    "}";

    @Test
    public void getCustomer_InvalidCustomerId_NotFound() throws Exception {
        String invalidCustomerId = UUID.randomUUID().toString();
        Mockito.when(customerService.getCustomer(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/customer/" + invalidCustomerId, 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCustomer_ValidCustomerId_StatusOk() throws Exception {
        CustomerResponse validCustomerResponse = createValidCustomerResponse();
        Mockito.when(customerService.getCustomer(any())).thenReturn(validCustomerResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/customer/" + validCustomerResponse.getId(), 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void postCustomer_InvalidCustomerRequest_BadRequest() throws Exception {
        CustomerResponse invalidCustomerResponse = createInvalidCustomerResponse();
        Mockito.when(customerService.postCustomer(any())).thenReturn(invalidCustomerResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/customer", 1)
                        .content(invalidCustomerRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postCustomer_ValidCustomerRequest_StatusOk() throws Exception {
        CustomerResponse validCustomerResponse = createValidCustomerResponse();
        Mockito.when(customerService.postCustomer(any())).thenReturn(validCustomerResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/customer", 1)
                        .content(validCustomerRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void putCustomer_InvalidCustomerRequest_BadRequest() throws Exception {
        CustomerResponse invalidCustomerResponse = createInvalidCustomerResponse();
        Mockito.when(customerService.putCustomer(any(), any())).thenReturn(invalidCustomerResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/customer/" + invalidCustomerResponse.getId(), 2)
                        .content(invalidCustomerRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putCustomer_ValidCustomerRequest_StatusOk() throws Exception {
        CustomerResponse validCustomerResponse = createValidCustomerResponse();
        Mockito.when(customerService.putCustomer(any(), any())).thenReturn(validCustomerResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/customer/" + validCustomerResponse.getId(), 2)
                        .content(validCustomerRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    public void deleteCustomer_ExistingCustomer_StatusOk() throws Exception {
        String invalidCustomerId = UUID.randomUUID().toString();
        Mockito.doNothing().when(customerService).deleteCustomer(any());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/customer/" + invalidCustomerId, 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Bob");
        customer.setLastName("Bobin");
        customer.setEmail("bbobin@gmail.com");

        return customer;
    }

    private CustomerResponse createValidCustomerResponse() {
        Customer customer = createValidCustomer();

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(customer.getId().toString());
        customerResponse.setFirstName(customer.getFirstName());
        customerResponse.setLastName(customer.getLastName());
        customerResponse.setEmail(customer.getEmail());

        return customerResponse;
    }

    private Customer createInvalidCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("");
        customer.setLastName("");
        customer.setEmail("");

        return customer;
    }

    private CustomerResponse createInvalidCustomerResponse() {
        Customer customer = createInvalidCustomer();

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(customer.getId().toString());
        customerResponse.setFirstName(customer.getFirstName());
        customerResponse.setLastName(customer.getLastName());
        customerResponse.setEmail(customer.getEmail());

        return customerResponse;
    }
}
