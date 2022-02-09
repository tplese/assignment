package com.webshop.assignment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "webshop_order" )
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = "total_price_hrk")
    private BigDecimal totalPriceHrk;

    @Column(name = "total_price_eur")
    private BigDecimal totalPriceEur;

    public enum Status {
        DRAFT, SUBMITTED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Order order = (Order) o;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
