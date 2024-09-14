package com.ayush.ayush.repository;

import com.ayush.ayush.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SellerRepositoryJpa extends JpaRepository<Seller,Long>{

    Optional<Seller> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Seller s set s.password = :newPassword where s.id = :id
""")
    void updatePassword(String newPassword, Long id);

}
