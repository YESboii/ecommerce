package com.ayush.ayush.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {@Index(name="seller_key_index",columnList = "reg_key")})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SellerActivation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reg_key",nullable = false, unique = true)
    private String key;
    private LocalDateTime expiryTime;
    @ManyToOne
    @JoinColumn(name = "seller_id",nullable = false)
    private Seller seller;

}
