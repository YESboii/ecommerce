package com.ayush.ayush.repository;

import com.ayush.ayush.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long>{
    Optional<Customer> findByUsername(String username);

    @Transactional(readOnly = true)
    @Query("""
    SELECT c.id from Customer c where c.username = :username
""")

    Optional<Long> findIdByUsername(String username);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Customer c set c.password = :encodedNewPassword where c.id = :id
""")

    void updatePassword(String encodedNewPassword, Long id);
}
