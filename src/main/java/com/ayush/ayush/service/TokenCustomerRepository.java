package com.ayush.ayush.service;
import com.ayush.ayush.model.TokenCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenCustomerRepository extends JpaRepository<TokenCustomer, Long>{

    @Transactional(readOnly = true)
    Optional<TokenCustomer> findByJwt(String jwt);

    @Transactional
    @Modifying
    @Query("""
            UPDATE TokenCustomer t SET t.isRevoked = TRUE WHERE t.customer.id = :customerId AND t.isRevoked = FALSE
""")
    Integer revokeAllTokensByCustomerId(Long customerId);
}
