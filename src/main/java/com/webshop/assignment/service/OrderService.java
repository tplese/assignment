package com.webshop.assignment.service;

import com.webshop.assignment.controller.request.OrderItemRequest;
import com.webshop.assignment.controller.response.OrderItemResponse;
import com.webshop.assignment.controller.response.OrderResponse;
import com.webshop.assignment.model.Customer;
import com.webshop.assignment.model.Order;
import com.webshop.assignment.model.OrderItem;
import com.webshop.assignment.model.Product;
import com.webshop.assignment.repository.CustomerRepository;
import com.webshop.assignment.repository.OrderItemRepository;
import com.webshop.assignment.repository.OrderRepository;
import com.webshop.assignment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final RateService rateService;

    public Boolean checkIfAllProductsAvailable(List<OrderItemRequest> orderItemRequestList) {
        final Boolean[] allProductsAvailable = {Boolean.TRUE};

        orderItemRequestList.forEach(item -> {
            Optional<Product> product = productRepository.findById(UUID.fromString(item.getProductId()));

            if (product.isEmpty() || Boolean.FALSE.equals(product.get().getIsAvailable())) {
                allProductsAvailable[0] = Boolean.FALSE;
            }
        });

        return allProductsAvailable[0];
    }

    public Order createOrder(String customerId) {
        Optional<Customer> customer = customerRepository.findById(UUID.fromString(customerId));

        Order order = new Order();
        if (customer.isPresent()) {
            order.setCustomer(customer.get());
            order.setStatus(Order.Status.DRAFT);

            return order;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
        }
    }

    public void saveOrderItemsToDb(List<OrderItemRequest> orderItemRequestList, String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));

        if (order.isPresent()) {
            orderItemRequestList.forEach(item -> {
                Optional<Product> product = productRepository.findById(UUID.fromString(item.getProductId()));

                if (product.isPresent() && Boolean.TRUE.equals(product.get().getIsAvailable())) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order.get());
                    orderItem.setProduct(product.get());
                    orderItem.setQuantity(item.getQuantity());

                    orderItemRepository.save(orderItem);
                }
            });
        }
    }

    public BigDecimal updateTotalPriceInHrk(String orderId) {
        BigDecimal totalPriceHrk = calculateTotalPriceInHrk(orderId);

        // If totalPriceHrk is greater than 0 => comparedValues = 1
        int comparedValues = totalPriceHrk.compareTo(new BigDecimal("0.00"));
        if (comparedValues == 1) {
            return totalPriceHrk;
        } else {
            return BigDecimal.valueOf(0.00);
        }
    }

    public BigDecimal updateTotalPriceInEur(BigDecimal totalPriceHrk) {
        BigDecimal totalPriceEur = BigDecimal.valueOf(0.00);

        // If totalPriceEur is greater than 0 => comparedValues = 1
        int comparedValues = totalPriceHrk.compareTo(new BigDecimal("0.00"));
        if (comparedValues == 1) {
            totalPriceEur = convertHrkToEur(totalPriceHrk);
        }

        return totalPriceEur;
    }

    public List<OrderItem> findItemsFromAnOrder(String orderId) {
        ArrayList<OrderItem> listOrderItemsFromAnOrder = new ArrayList<>();

        listOrderItemsFromAnOrder.addAll(orderItemRepository.findOrderItemsByOrder_Id(UUID.fromString(orderId)));

        return listOrderItemsFromAnOrder;
    }

    public List<OrderItemResponse> populateOrderItemResponseList(String orderId) {
        List<OrderItem> orderItemList = findItemsFromAnOrder(orderId);

        ArrayList<OrderItemResponse> orderItemResponseList = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setProductId(orderItem.getProduct().getId().toString());
            orderItemResponse.setProductName(orderItem.getProduct().getName());
            orderItemResponse.setProductPriceHrk(orderItem.getProduct().getPriceHrk());
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponse.setTotalItemPriceHrk(calculateItemPriceInHrk(orderItem));
            orderItemResponseList.add(orderItemResponse);
        }

        return orderItemResponseList;
    }

    public BigDecimal calculateItemPriceInHrk(OrderItem orderItem) {
        BigDecimal productPrice = orderItem.getProduct().getPriceHrk();
        BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());

        return productPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalPriceInHrk(String orderId) {
        List<OrderItem> itemsInAnOrder = findItemsFromAnOrder(orderId);

        BigDecimal totalPriceInHrk = BigDecimal.valueOf(0.00);

        for (OrderItem item : itemsInAnOrder) {
            totalPriceInHrk = totalPriceInHrk.add(calculateItemPriceInHrk(item));
        }

        return totalPriceInHrk.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal convertHrkToEur(BigDecimal totalPriceInHrk) {
        BigDecimal eurBuyingRate = rateService.getRateForEur();
        BigDecimal totalPriceInEur = totalPriceInHrk.divide(eurBuyingRate, RoundingMode.HALF_UP);
        return totalPriceInEur.setScale(2, RoundingMode.HALF_UP);
    }

    public OrderResponse createOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setCustomerId(order.getCustomer().getId().toString());
        orderResponse.setCustomerFirstName(order.getCustomer().getFirstName());
        orderResponse.setCustomerLastName(order.getCustomer().getLastName());
        orderResponse.setOrderId(order.getId().toString());
        orderResponse.setStatus(order.getStatus());

        List<OrderItemResponse> orderItemResponseList = populateOrderItemResponseList(order.getId().toString());
        orderResponse.setOrderItemList(orderItemResponseList);

        return orderResponse;
    }

    public OrderResponse createSubmittedOrderResponse(Order order) {
        OrderResponse orderResponse = createOrderResponse(order);
        orderResponse.setTotalPriceHrk(order.getTotalPriceHrk());
        orderResponse.setTotalPriceEur(order.getTotalPriceEur());

        return orderResponse;
    }
}
