package com.ayush.ayush.service;

import com.ayush.ayush.model.TokenSeller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenSellerRepository extends JpaRepository<TokenSeller, Long> {

    @Transactional(readOnly = true)
    Optional<TokenSeller> findByJwt(String jwt);

    @Transactional
    @Modifying
    @Query("""
           UPDATE TokenSeller t set t.isRevoked = TRUE where t.seller.id = :sellerId and t.isRevoked = FALSE
            """)
    Integer revokeAllTokensBySellerId(Long sellerId);
}
