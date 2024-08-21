package com.ayush.ayush.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@Table(indexes = {@Index(name="username_idx",columnList = "username"), @Index(name="phone_idx", columnList = "phoneNumber")})
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Customer extends AppUser{
    @OneToMany(mappedBy = "customer",fetch = FetchType.LAZY)
    private Set<Order> orders;
}
