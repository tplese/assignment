package com.webshop.assignment.repository;

import com.webshop.assignment.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
}
