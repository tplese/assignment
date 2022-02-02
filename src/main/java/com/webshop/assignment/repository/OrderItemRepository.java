package com.webshop.assignment.repository;

import com.webshop.assignment.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends CrudRepository<OrderItem, UUID> {
    List<OrderItem> findOrderItemsByOrder_Id(UUID id);
}
