package com.example.demo.repository;

import com.example.demo.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    boolean existsByProductId(Long productId);
}
