package com.ayush.ayush.model;

import com.ayush.ayush.model.embeddedable.TokenType;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(indexes = @Index(columnList = "jwt",name = "customer_idx_token"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenCustomer {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String jwt;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean isRevoked;
}
