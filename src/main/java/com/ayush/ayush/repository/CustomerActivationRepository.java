package com.ayush.ayush.repository;

import com.ayush.ayush.model.Customer;
import com.ayush.ayush.model.CustomerActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CustomerActivationRepository extends JpaRepository<CustomerActivation, Long> {
    @Modifying
    @Transactional
    @Query("""
            select c from CustomerActivation ca join ca.customer c where ca.key = :key
            """)
    Optional<Customer> findCustomerByActivationKey(String key);

}
