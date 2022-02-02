package com.webshop.assignment.controller;

import com.webshop.assignment.controller.response.OrderItemResponse;
import com.webshop.assignment.controller.response.OrderResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.model.Order;
import com.webshop.assignment.service.OrderResponseService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.webshop.assignment.model.Order.Status.DRAFT;
import static com.webshop.assignment.model.Order.Status.SUBMITTED;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerMvcTest {

    @MockBean
    private OrderResponseService orderResponseService;;

    @Autowired
    private MockMvc mockMvc;

    private final String validOrderRequest =
            "{" +
                    "\"customerId\": \"31269d6a-be2e-4400-9c3f-05f1bca9f39a\", " +
                    "\"orderItemList\": [" +
                    "{\"productId\": \"1b1cf2a9-3618-4827-aa86-8206dbfee702\", " +
                    "\"quantity\": 2}, " +
                    "{\"productId\": \"9daf93c7-ee52-40c8-b404-c6080bc72d09\", " +
                    "\"quantity\": 50}" +
                    "]}";

    private final String invalidOrderRequest =
            "{" +
                    "\"customerId\": \"\", " +
                    "\"orderItemList\": \"\" " +
                    "}";

    @Test
    public void getOrder_InvalidOrderId_NotFound() throws Exception {
        String invalidOrderId = UUID.randomUUID().toString();
        Mockito.when(orderResponseService.getOrder(any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/read-order/" + invalidOrderId, 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrder_ValidOrderIdStatusDraft_StatusOk() throws Exception {
        OrderResponse validOrderResponse = createValidOrderResponse(DRAFT);
        Mockito.when(orderResponseService.getOrder(any())).thenReturn(validOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/read-order/" + validOrderResponse.getOrderId(), 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").exists());
    }

    @Test
    public void getOrder_ValidOrderIdStatusSubmitted_StatusOk() throws Exception {
        OrderResponse validOrderResponse = createValidOrderResponse(SUBMITTED);
        Mockito.when(orderResponseService.getOrder(any())).thenReturn(validOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/read-order/" + validOrderResponse.getOrderId(), 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").exists());
    }

    @Test
    public void submitOrder_InvalidOrderId_NotFound() throws Exception {
        String invalidOrderId = UUID.randomUUID().toString();
        Mockito.when(orderResponseService.submitOrder(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/submit-order" + invalidOrderId, 1)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void submitOrder_ValidOrderId_StatusOk() throws Exception {
        OrderResponse validOrderResponse = createValidOrderResponse(SUBMITTED);
        Mockito.when(orderResponseService.submitOrder(any())).thenReturn(validOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/submit-order/" + validOrderResponse.getOrderId(), 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").exists());
    }

    @Test
    public void postOrder_InvalidOrderRequest_BadRequest() throws Exception {
        OrderResponse invalidOrderResponse = createInvalidOrderResponse();
        Mockito.when(orderResponseService.postOrder(any())).thenReturn(invalidOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/create-order", 1)
                        .content(invalidOrderRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postOrder_ValidOrderRequest_StatusOk() throws Exception {
        OrderResponse validOrderResponse = createValidOrderResponse(DRAFT);
        Mockito.when(orderResponseService.postOrder(any())).thenReturn(validOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/create-order", 1)
                        .content(validOrderRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").exists());
    }

    @Test
    public void putOrder_InvalidOrderRequest_BadRequest() throws Exception {
        OrderResponse invalidOrderResponse = createInvalidOrderResponse();
        Mockito.when(orderResponseService.putOrder(any(), any())).thenReturn(invalidOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update-order/" + invalidOrderResponse.getOrderId(), 2)
                        .content(invalidOrderRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putOrder_ValidOrderRequest_StatusOk() throws Exception {
        OrderResponse validOrderResponse = createValidOrderResponse(DRAFT);
        Mockito.when(orderResponseService.putOrder(any(), any())).thenReturn(validOrderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update-order/" + validOrderResponse.getOrderId(), 2)
                        .content(validOrderRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").exists());
    }

    @Test
    public void deleteOrder_ExistingOrder_StatusOk() throws Exception {
        String validOrderId = UUID.randomUUID().toString();
        Mockito.doNothing().when(orderResponseService).deleteOrder(any());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/delete-order/" + validOrderId, 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /* *** VALID ORDER *** */
    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName("Bob");
        customer.setLastName("Bobin");
        customer.setEmail("bbobin@gmail.com");

        return customer;
    }

    private Order createValidOrder(Order.Status status) {
        Customer validCustomer = createValidCustomer();

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomer(validCustomer);
        order.setStatus(status);

        if (order.getStatus().equals(SUBMITTED)) {
            order.setTotalPriceHrk(BigDecimal.valueOf(750.00));
            order.setTotalPriceEur(BigDecimal.valueOf(100.00));
        }

        return order;
    }

    private List<OrderItemResponse> createOrderItemResponseList() {
        List<OrderItemResponse> orderItemResponseList = new ArrayList<>();

        OrderItemResponse orderItemResponseOne = new OrderItemResponse();
        orderItemResponseOne.setProductId(UUID.randomUUID().toString());
        orderItemResponseOne.setProductName("Hammer");
        orderItemResponseOne.setQuantity(2L);
        orderItemResponseOne.setProductPriceHrk(BigDecimal.valueOf(50.00));
        orderItemResponseOne.setTotalItemPriceHrk(BigDecimal.valueOf(100.00));

        orderItemResponseList.add(orderItemResponseOne);

        OrderItemResponse orderItemResponseTwo = new OrderItemResponse();
        orderItemResponseTwo.setProductId(UUID.randomUUID().toString());
        orderItemResponseTwo.setProductName("Hammer");
        orderItemResponseTwo.setQuantity(2L);
        orderItemResponseTwo.setProductPriceHrk(BigDecimal.valueOf(50.00));
        orderItemResponseTwo.setTotalItemPriceHrk(BigDecimal.valueOf(100.00));

        orderItemResponseList.add(orderItemResponseTwo);

        return orderItemResponseList;
    }

    private OrderResponse createValidOrderResponse(Order.Status status) {
        Order validOrder = createValidOrder(status);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setCustomerId(validOrder.getCustomer().getId().toString());
        orderResponse.setCustomerFirstName(validOrder.getCustomer().getFirstName());
        orderResponse.setCustomerLastName(validOrder.getCustomer().getLastName());
        orderResponse.setOrderId(validOrder.getId().toString());
        orderResponse.setStatus(status);

        if (SUBMITTED.equals(validOrder.getStatus())) {
            orderResponse.setTotalPriceHrk(validOrder.getTotalPriceHrk());
            orderResponse.setTotalPriceEur(validOrder.getTotalPriceEur());
        }

        List<OrderItemResponse> orderItemResponseList = createOrderItemResponseList();
        orderResponse.setOrderItemList(orderItemResponseList);

        return orderResponse;
    }

    /* *** INVALID ORDER *** */
    private OrderResponse createInvalidOrderResponse() {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(UUID.randomUUID().toString());

        return orderResponse;
    }
}
