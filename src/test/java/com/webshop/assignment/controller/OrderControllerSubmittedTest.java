package com.webshop.assignment.controller;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.webshop.assignment.controller.request.OrderItemRequest;
import com.webshop.assignment.controller.request.OrderRequest;
import com.webshop.assignment.controller.response.OrderResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.repository.CustomerRepository;
import com.webshop.assignment.repository.OrderItemRepository;
import com.webshop.assignment.repository.OrderRepository;
import com.webshop.assignment.repository.ProductRepository;
import com.webshop.assignment.service.OrderService;
import org.apache.hc.core5.http.ContentType;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.webshop.assignment.model.Order.Status.SUBMITTED;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8889)
class OrderControllerSubmittedTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

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
        productRepository.deleteAll();
        customerRepository.deleteAll();

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

        String stubBody = "[{\"Broj tečajnice\":\"18\"," +
                "\"Datum primjene\":\"27.01.2022\"," +
                "\"Država\":\"EMU\"," +
                "\"Šifra valute\":\"978\"," +
                "\"Valuta\":\"EUR\"," +
                "\"Jedinica\":1," +
                "\"Kupovni za devize\":\"7,500000\"," +
                "\"Srednji za devize\":\"7,526928\"," +
                "\"Prodajni za devize\":\"7,549509\"}]";

        stubFor(get(WireMock.urlEqualTo("/eur"))
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(HttpStatus.OK.ordinal())
                        .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                        .withBody(stubBody)));
    }

    @Test
    void getOrder_OrderStatusSubmitted_OrderReturned() {
        // arrange
        HttpEntity<OrderRequest> postOrderRequest = new HttpEntity<>(createOrderRequest());
        OrderResponse postOrderResponseBody = getOrderResponseFromPost(postOrderRequest);

        // act
        String submitUrl = "http://localhost:" + port + "/api/v1/submit-order/" + postOrderResponseBody.getOrderId();
        ResponseEntity<OrderResponse> submitOrderResponse =
                testRestTemplate.exchange(submitUrl, HttpMethod.POST, postOrderRequest, OrderResponse.class);
        OrderResponse submitOrderResponseBody = submitOrderResponse.getBody();

        String url = "http://localhost:" + port + "/api/v1/read-order/" + postOrderResponseBody.getOrderId();
        ResponseEntity<OrderResponse> orderResponse = testRestTemplate.getForEntity(url, OrderResponse.class);
        OrderResponse orderResponseBody = orderResponse.getBody();

        // assert
        assert orderResponseBody != null;
        assertThat(orderResponseBody.getOrderId()).isEqualTo(postOrderResponseBody.getOrderId());
        assertThat(orderResponseBody.getCustomerId()).isEqualTo(customerId);
        assertThat(orderResponseBody.getStatus()).isEqualTo(SUBMITTED);
        assert submitOrderResponseBody != null;
        assertThat(orderResponseBody.getTotalPriceHrk()).isEqualTo(submitOrderResponseBody.getTotalPriceHrk());
        assertThat(orderResponseBody.getTotalPriceEur()).isEqualTo(submitOrderResponseBody.getTotalPriceEur());
    }

    @Test
    void submitOrder_OrderStatusSubmitted_OrderFinalized() {
        // arrange
        HttpEntity<OrderRequest> postRequest = new HttpEntity<>(createOrderRequest());
        OrderResponse postOrderResponseBody = getOrderResponseFromPost(postRequest);

        BigDecimal testTotalPriceHrk = orderService.calculateTotalPriceInHrk(postOrderResponseBody.getOrderId());
        BigDecimal testTotalPriceEur = orderService.convertHrkToEur(testTotalPriceHrk);

        OrderRequest orderRequest = new OrderRequest();
        HttpEntity<OrderRequest> request = new HttpEntity<>(orderRequest);

        // act
        String url = "http://localhost:" + port + "/api/v1/submit-order/" + postOrderResponseBody.getOrderId();
        ResponseEntity<OrderResponse> orderResponse =
                testRestTemplate.exchange(url, HttpMethod.POST, request, OrderResponse.class);
        OrderResponse orderResponseBody = orderResponse.getBody();

        // assert
        assert orderResponseBody != null;
        assertThat(orderResponseBody.getOrderId()).isEqualTo(postOrderResponseBody.getOrderId());
        assertThat(orderResponseBody.getCustomerId()).isEqualTo(customerId);
        assertThat(orderResponseBody.getStatus()).isEqualTo(SUBMITTED);
        assertThat(orderResponseBody.getTotalPriceHrk()).isEqualTo(testTotalPriceHrk);
        assertThat(orderResponseBody.getTotalPriceEur()).isEqualTo(testTotalPriceEur);
    }

    private OrderRequest createOrderRequest() {
        OrderItemRequest orderItemRequestOne = new OrderItemRequest();
        orderItemRequestOne.setProductId(productOne.getId().toString());
        orderItemRequestOne.setQuantity(2L);

        OrderItemRequest orderItemRequestTwo = new OrderItemRequest();
        orderItemRequestTwo.setProductId(productTwo.getId().toString());
        orderItemRequestTwo.setQuantity(50L);

        ArrayList<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        orderItemRequestList.add(orderItemRequestOne);
        orderItemRequestList.add(orderItemRequestTwo);

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
