package com.ayush.ayush.model;

import com.ayush.ayush.model.embeddedable.TokenType;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(indexes = @Index(columnList = "jwt",name = "seller_idx_token"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenSeller {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String jwt;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "seller_id")
    private Seller seller;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean isRevoked;

}
