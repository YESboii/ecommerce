package com.ayush.ayush.repository;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.model.SellerActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SellerActivationRepository extends JpaRepository<SellerActivation,Long> {

    @Modifying
    @Transactional
    @Query("""
            SELECT s FROM SellerActivation sa join sa.seller s where sa.key = :key
            """)
    Optional<Seller> findSellerByActivationKey(String key);
}
