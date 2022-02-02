package com.webshop.assignment.service;

import com.webshop.assignment.controller.request.OrderRequest;
import com.webshop.assignment.controller.response.OrderResponse;
import com.webshop.assignment.model.Order;
import com.webshop.assignment.model.OrderItem;
import com.webshop.assignment.repository.OrderItemRepository;
import com.webshop.assignment.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.webshop.assignment.model.Order.Status.DRAFT;
import static com.webshop.assignment.model.Order.Status.SUBMITTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderResponseService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    public OrderResponse getOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));

        if (order.isPresent()) {
            if (DRAFT.equals(order.get().getStatus())) {
                return orderService.createOrderResponse(order.get());
            } else if (SUBMITTED.equals(order.get().getStatus())){
                return orderService.createSubmittedOrderResponse(order.get());
            } else {
                throw new ResponseStatusException(BAD_REQUEST, "Unknown Order status.");
            }
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
    }

    public OrderResponse submitOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));

        if (order.isPresent()) {
            if (SUBMITTED.equals(order.get().getStatus())) {
                return orderService.createOrderResponse(order.get());
            }

            order.get().setStatus(Order.Status.SUBMITTED);

            BigDecimal totalPriceHrk = orderService.updateTotalPriceInHrk(order.get().getId().toString());
            order.get().setTotalPriceHrk(totalPriceHrk);
            BigDecimal totalPriceEur = orderService.updateTotalPriceInEur(totalPriceHrk);
            order.get().setTotalPriceEur(totalPriceEur);

            orderRepository.save(order.get());

            return orderService.createSubmittedOrderResponse(order.get());
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
    }

    public OrderResponse postOrder(OrderRequest orderRequest) {
        Boolean allProductsAvailable = orderService.checkIfAllProductsAvailable(orderRequest.getOrderItemList());

        if (Boolean.TRUE.equals(allProductsAvailable)) {
            Order order = orderService.createOrder(orderRequest.getCustomerId());

            orderRepository.save(order);

            orderService.saveOrderItemsToDb(orderRequest.getOrderItemList(), order.getId().toString());

            return orderService.createOrderResponse(order);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource unavailable");
        }
    }

    public OrderResponse putOrder(String orderId, OrderRequest orderRequest) {
        Boolean allProductsAvailable = orderService.checkIfAllProductsAvailable(orderRequest.getOrderItemList());

        if (Boolean.TRUE.equals(allProductsAvailable)) {

            Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));

            if (order.isPresent()) {
                if (DRAFT.equals(order.get().getStatus())) {
                    orderService.saveOrderItemsToDb(orderRequest.getOrderItemList(), orderId);
                }

                return orderService.createOrderResponse(order.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource unavailable");
        }
    }

    public void deleteOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(UUID.fromString(orderId));

        List<OrderItem> orderItemList = orderService.findItemsFromAnOrder(orderId);
        orderItemRepository.deleteAll(orderItemList);

        order.ifPresent(orderRepository::delete);
    }
}
