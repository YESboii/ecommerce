package com.ayush.ayush.model;

import com.ayush.ayush.model.embeddedable.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
@Table(name = "app_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderUuid;

    private LocalDateTime orderDateTime;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER)
    private List<ProductOrder> productOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
