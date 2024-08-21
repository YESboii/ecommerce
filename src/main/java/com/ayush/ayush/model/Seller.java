package com.ayush.ayush.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Table(indexes = {@Index(name="username_idx",columnList = "username"), @Index(name="phone_idx", columnList = "phoneNumber")})
public class Seller extends AppUser{
    @Column(unique = true, nullable = false)
    private String gstId;

    @ManyToMany
    @JoinTable(name = "seller_category_mapping",
    joinColumns = @JoinColumn(name = "seller_id"), inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<SellerCategory> categories;

    @OneToMany(mappedBy = "seller", orphanRemoval = true,cascade = CascadeType.ALL)
    private Set<Product> products;
}
