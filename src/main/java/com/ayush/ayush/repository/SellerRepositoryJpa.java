package com.ayush.ayush.repository;

import com.ayush.ayush.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepositoryJpa extends JpaRepository<Seller,Long>{

    Optional<Seller> findByUsername(String username);
}
