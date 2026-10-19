package com.example.demo.repository;

import com.example.demo.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    boolean existsByProductId(Long productId);

    @Query("""
    SELECT COALESCE(SUM(b.quantity), 0)
    FROM Batch b
    WHERE b.product.id = :productId
    """)
    Long sumQuantityByProductId(@Param("productId") Long productId);
}
