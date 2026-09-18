package com.example.demo.repository;

import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductTypeId(Long ProductTypeId);
    boolean existsByVendorId(Long vendorId);


    @Modifying
    @Query("""
       UPDATE Product p
       SET p.activeStatus = :activeStatus
       WHERE p.id = :productId
       """)
    void setActiveStatusByProductId(
            @Param("activeStatus") ActiveStatus activeStatus,
            @Param("productId") Long productId
    );
}
