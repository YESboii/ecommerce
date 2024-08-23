package com.ayush.ayush.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(indexes = {@Index(name="username_idx",columnList = "username"), @Index(name="phone_idx", columnList = "phoneNumber")})
public class Seller extends AppUser{
    @Column(unique = true, nullable = false)
    private String gstId;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(name = "seller_category_mapping",
    joinColumns = @JoinColumn(name = "seller_id"), inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<SellerCategory> categories;

    @OneToMany(mappedBy = "seller", orphanRemoval = true,cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Product> products;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seller seller = (Seller) o;
        return getId() == seller.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
