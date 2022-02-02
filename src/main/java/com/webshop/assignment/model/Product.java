package com.webshop.assignment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "product_code", unique = true, length = 10)
    @Size(min = 10, max = 10)
    private String code;

    private String name;

    @Column(name = "price_hrk")
    @DecimalMin("0.00")
    private BigDecimal priceHrk;

    private String description;

    @Column(name = "is_available")
    private Boolean isAvailable;
}
