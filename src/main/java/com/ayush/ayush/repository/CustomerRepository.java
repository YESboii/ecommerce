package com.ayush.ayush.repository;

import com.ayush.ayush.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long>{
    Optional<Customer> findByUsername(String username);
}
