package com.webshop.assignment.repository;

import com.webshop.assignment.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
}
