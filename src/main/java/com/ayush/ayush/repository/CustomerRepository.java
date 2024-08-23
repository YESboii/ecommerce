package com.ayush.ayush.repository;

import com.ayush.ayush.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{
}
