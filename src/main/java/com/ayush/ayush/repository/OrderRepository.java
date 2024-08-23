package com.ayush.ayush.repository;

import com.ayush.ayush.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
