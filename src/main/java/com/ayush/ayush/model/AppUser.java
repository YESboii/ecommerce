package com.ayush.ayush.model;

import com.ayush.ayush.model.embeddedable.Address;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public class AppUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;


    private Boolean twoFactorEnabled;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Embedded
    private Address address;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
