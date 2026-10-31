package com.example.demo.repository;

import com.example.demo.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    boolean existsByProductId(Long productId);

    @Query("""
    SELECT COALESCE(SUM(b.quantity), 0)
    FROM Batch b
    WHERE b.product.id = :productId
    """)
    Long sumQuantityByProductId(@Param("productId") Long productId);

    @Query("""
    SELECT b.product.id AS productId, COALESCE(SUM(b.quantity), 0) AS qty
    FROM Batch b
    WHERE b.activeStatus = com.example.demo.entities.ActiveStatus.ACTIVE
      AND b.product.id IN :productIds
    GROUP BY b.product.id
    """)
    List<ProductQuantityView> sumActiveQuantityByProductIds(@Param("productIds") List<Long> productIds);



    interface ProductQuantityView {
        Long getProductId();
        Long getQty();
    }
}
