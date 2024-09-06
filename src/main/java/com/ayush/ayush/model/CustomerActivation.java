package com.ayush.ayush.model;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;
@Entity
@Table(indexes = {})
public class CustomerActivation {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String key;
    @OneToOne
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;
}
