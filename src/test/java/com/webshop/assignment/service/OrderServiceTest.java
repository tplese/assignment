package com.webshop.assignment.service;

import com.webshop.assignment.controller.request.OrderItemRequest;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.model.Order;
import com.webshop.assignment.model.OrderItem;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.repository.CustomerRepository;
import com.webshop.assignment.repository.OrderItemRepository;
import com.webshop.assignment.repository.OrderRepository;
import com.webshop.assignment.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.webshop.assignment.model.Order.Status.DRAFT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    private static final Customer newCustomer = new Customer();;
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

        newCustomer.setFirstName("Carl");
        newCustomer.setLastName("Carlin");
        newCustomer.setEmail("ccarlin@gmai.com");

        customerRepository.save(newCustomer);

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

    @Test
    void saveOrderItemsToDb_ProductsAvailable_OrderItemsSaved() {
        // arrange
        List<OrderItemRequest> orderItemRequestList = createListOfOrderItems();

        Order newOrder = new Order();
        newOrder.setCustomer(newCustomer);
        newOrder.setStatus(DRAFT);

        orderRepository.save(newOrder);

        // act
        orderService.saveOrderItemsToDb(orderItemRequestList, newOrder.getId().toString());
        ArrayList<OrderItem> orderItemList = new ArrayList<>();

        orderItemRepository.findOrderItemsByOrder_Id(newOrder.getId()).forEach(item -> orderItemList.add(item));

        // assert
        assertThat(orderItemList.size()).isEqualTo(2);
        assertThat(orderItemList.get(0).getProduct().getId().toString())
                .hasToString(productOne.getId().toString());
        assertThat(orderItemList.get(1).getProduct().getId().toString())
                .hasToString(productTwo.getId().toString());
    }

    public List<OrderItemRequest> createListOfOrderItems() {
        OrderItemRequest orderItemRequestOne = new OrderItemRequest();
        orderItemRequestOne.setProductId(productOne.getId().toString());
        orderItemRequestOne.setQuantity(2L);

        OrderItemRequest orderItemRequestTwo = new OrderItemRequest();
        orderItemRequestTwo.setProductId(productTwo.getId().toString());
        orderItemRequestTwo.setQuantity(50L);

        ArrayList<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        orderItemRequestList.add(orderItemRequestOne);
        orderItemRequestList.add(orderItemRequestTwo);

        return orderItemRequestList;
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
