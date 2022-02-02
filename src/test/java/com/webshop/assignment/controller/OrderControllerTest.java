package com.webshop.assignment.controller;

import com.webshop.assignment.controller.request.OrderItemRequest;
import com.webshop.assignment.controller.request.OrderRequest;
import com.webshop.assignment.controller.response.OrderResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.repository.CustomerRepository;
import com.webshop.assignment.repository.OrderItemRepository;
import com.webshop.assignment.repository.OrderRepository;
import com.webshop.assignment.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static com.webshop.assignment.model.Order.Status.DRAFT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static String customerId;
    private static final Product productOne = new Product();
    private static final Product productTwo = new Product();

    @BeforeAll
    static void beforeAll(@Autowired CustomerRepository customerRepository,
                          @Autowired ProductRepository productRepository,
                          @Autowired OrderRepository orderRepository,
                          @Autowired OrderItemRepository orderItemRepository) {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();

        Customer newCustomer = new Customer();
        newCustomer.setFirstName("Carl");
        newCustomer.setLastName("Carlin");
        newCustomer.setEmail("ccarlin@gmai.com");

        Customer newCustomerSavedToDb = customerRepository.save(newCustomer);
        customerId = newCustomerSavedToDb.getId().toString();

        productOne.setCode("1111111111");
        productOne.setName("Hammer");
        productOne.setPriceHrk(BigDecimal.valueOf(149.99));
        productOne.setDescription("Weighs 10 kg");
        productOne.setIsAvailable(true);

        productRepository.save(productOne);

        productTwo.setCode("2222222222");
        productTwo.setName("Nail");
        productTwo.setPriceHrk(BigDecimal.valueOf(1.99));
        productTwo.setDescription("7cm long");
        productTwo.setIsAvailable(true);

        productRepository.save(productTwo);
    }

    @BeforeEach
    public void beforeEach() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void getOrder_ValidOrderId_OrderReturned() {
        // arrange
        HttpEntity<OrderRequest> postRequest = new HttpEntity<>(createOrderRequest());
        OrderResponse postOrderResponseBody = getOrderResponseFromPost(postRequest);

        // act
        String url = "http://localhost:" + port + "/api/v1/read-order/" + postOrderResponseBody.getOrderId();
        ResponseEntity<OrderResponse> orderResponse = testRestTemplate.getForEntity(url, OrderResponse.class);
        OrderResponse orderResponseBody = orderResponse.getBody();

        // assert
        assert orderResponseBody != null;
        assertThat(orderResponseBody.getOrderId()).isEqualTo(postOrderResponseBody.getOrderId());
        assertThat(orderResponseBody.getCustomerId()).isEqualTo(customerId);
        assertThat(orderResponseBody.getStatus()).isEqualTo(DRAFT);
    }

    @Test
    void postOrder_ValidOrderRequest_OrderCreated() {
        // arrange
        HttpEntity<OrderRequest> request = new HttpEntity<>(createOrderRequest());

        // act
        String url = "http://localhost:" + port + "/api/v1/create-order";
        ResponseEntity<OrderResponse> orderResponse =
                testRestTemplate.exchange(url, HttpMethod.POST, request, OrderResponse.class);
        OrderResponse orderResponseBody = orderResponse.getBody();

        // assert
        assert orderResponseBody != null;
        assertThat(orderResponseBody.getOrderId()).isNotEmpty();
        assertThat(orderResponseBody.getCustomerId()).isEqualTo(customerId);
        assertThat(orderResponseBody.getStatus()).isEqualTo(DRAFT);
        assertThat(orderResponseBody.getTotalPriceHrk()).isNull();
        assertThat(orderResponseBody.getTotalPriceEur()).isNull();
        assertThat(orderResponseBody.getOrderItemList().get(0)
                .getProductId()).isEqualTo(productOne.getId().toString());
    }

    @Test
    void putOrder_ValidOrderRequestAndOrderId_OrderUpdated() {
        // arrange
        HttpEntity<OrderRequest> postRequest = new HttpEntity<>(createOrderRequest());
        OrderResponse postOrderResponseBody = getOrderResponseFromPost(postRequest);

        OrderItemRequest orderItemRequestTwo = new OrderItemRequest();
        orderItemRequestTwo.setProductId(productTwo.getId().toString());
        orderItemRequestTwo.setQuantity(30L);

        ArrayList<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        orderItemRequestList.add(orderItemRequestTwo);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setOrderItemList(orderItemRequestList);

        HttpEntity<OrderRequest> request = new HttpEntity<>(orderRequest);

        // act
        String url = "http://localhost:" + port + "/api/v1/update-order/" + postOrderResponseBody.getOrderId();
        ResponseEntity<OrderResponse> orderResponse =
                testRestTemplate.exchange(url, HttpMethod.PUT, request, OrderResponse.class);
        OrderResponse orderResponseBody = orderResponse.getBody();

        //assert
        assert orderResponseBody != null;
        assertThat(orderResponseBody.getOrderId()).isEqualTo(postOrderResponseBody.getOrderId());
        assertThat(orderResponseBody.getCustomerId()).isEqualTo(customerId);
        assertThat(orderResponseBody.getStatus()).isEqualTo(DRAFT);
        assertThat(orderResponseBody.getTotalPriceHrk()).isNull();
        assertThat(orderResponseBody.getTotalPriceEur()).isNull();
        assertThat(orderResponseBody.getOrderItemList().get(0)
                .getProductId()).isEqualTo(productOne.getId().toString());
        assertThat(orderResponseBody.getOrderItemList().get(1)
                .getProductId()).isEqualTo(productTwo.getId().toString());
    }

    @Test
    void deleteOrder_ValidOrderId_OrderDeleted() {
        // arrange
        HttpEntity<OrderRequest> postRequest = new HttpEntity<>(createOrderRequest());
        OrderResponse postOrderResponseBody = getOrderResponseFromPost(postRequest);

        ArrayList<String> orderItemIdList = new ArrayList<>();

        orderItemRepository.findOrderItemsByOrder_Id(UUID.fromString(postOrderResponseBody.getOrderId()))
                .forEach(item -> orderItemIdList.add(item.getId().toString()));

        // act
        String url = "http://localhost:" + port + "/api/v1/delete-order/" + postOrderResponseBody.getOrderId();
        testRestTemplate.delete(url);

        //assert
        assertThat(orderRepository.findById(UUID.fromString(postOrderResponseBody.getOrderId()))).isEmpty();
        assertThat(orderItemRepository.findById(UUID.fromString(orderItemIdList.get(0)))).isEmpty();
    }

    private OrderRequest createOrderRequest() {
        OrderItemRequest orderItemRequestOne = new OrderItemRequest();
        orderItemRequestOne.setProductId(productOne.getId().toString());
        orderItemRequestOne.setQuantity(2L);

        ArrayList<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        orderItemRequestList.add(orderItemRequestOne);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setOrderItemList(orderItemRequestList);

        return orderRequest;
    }

    private OrderResponse getOrderResponseFromPost(HttpEntity<OrderRequest> request) {
        String url = "http://localhost:" + port + "/api/v1/create-order";
        ResponseEntity<OrderResponse> orderResponse =
                testRestTemplate.exchange(url, HttpMethod.POST, request, OrderResponse.class);
        return orderResponse.getBody();
    }

    @AfterAll
    public static void afterAll(@Autowired CustomerRepository customerRepository,
                                @Autowired ProductRepository productRepository,
                                @Autowired OrderRepository orderRepository,
                                @Autowired OrderItemRepository orderItemRepository) {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
