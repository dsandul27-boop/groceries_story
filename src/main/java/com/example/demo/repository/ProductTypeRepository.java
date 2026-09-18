package com.example.demo.repository;

import com.example.demo.entities.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    boolean existsByCategoryId(Long categoryId);
}
