package com.example.demo.repository;

import com.example.demo.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByCartId(Long cartId);
    List<CartItem> findAllByCartId(Long cartId);
}
