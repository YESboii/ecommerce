package com.ayush.ayush.model;

import jakarta.persistence.*;

@Entity
@Table(indexes = {@Index(name="seller_key_index",columnList = "key")})
public class SellerActivation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String key;
    @OneToOne
    @JoinColumn(name = "seller_id",nullable = false)
    private Seller seller;
}
