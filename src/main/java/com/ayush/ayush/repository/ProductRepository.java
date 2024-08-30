package com.ayush.ayush.repository;

import com.ayush.ayush.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {

    @Transactional(readOnly = true)
    @Query("SELECT p from Product p join fetch p.categories where p.id=:id and p.seller.id = :sellerId")
    Optional<Product> findProductByIdAndSeller(int id, int sellerId);

    @Transactional(readOnly = true)
    @Query("SELECT p.id from Product p where p.seller.id = :sellerId")
    Page<Long> findProductIdsBySellerPagination(int sellerId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query("SELECT p from Product p join fetch p.categories c where p.id in :ids")
    List<Product> findProductsBySeller(List<Long> ids);


    @Modifying
    @Transactional(readOnly = false)
    @Query("DELETE from Product p where p.id=:id and p.seller.id = :sellerId")
    void deleteProductByIdAndSeller(int id, int sellerId);

    @Transactional(readOnly = true)
    @Query("SELECT p.image from Product p where p.id=:id and p.seller.id=:sellerId")
    String findImageBySellerIdAndId(int id,int sellerId);
}

