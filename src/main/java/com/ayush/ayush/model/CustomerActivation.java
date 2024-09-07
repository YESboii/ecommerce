package com.ayush.ayush.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
@Entity
@Table(indexes = {@Index(name = "customer_key_idx", columnList = "reg_key")})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CustomerActivation {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = "reg_key",nullable = false, unique = true)
    private String key;
    private LocalDateTime expiryTime;
    @ManyToOne
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;
}
