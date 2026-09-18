package com.example.demo.repositoy;

import com.example.demo.entities.*;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void setActiveStatusByProductId_shouldUpdateStatus() {

        // Arrange
        Category category = new Category();
        category.setCategoryName("Lactate");
        category.setActiveStatus(ActiveStatus.ACTIVE);
        category = entityManager.persistAndFlush(category);

        ProductType productType = new ProductType();
        productType.setActiveStatus(ActiveStatus.ACTIVE);
        productType.setName("Milk");
        productType.setCategory(category);
        productType = entityManager.persistAndFlush(productType);

        Vendor vendor = new Vendor();
        vendor.setActiveStatus(ActiveStatus.ACTIVE);
        vendor.setName("Latti");
        vendor = entityManager.persistAndFlush(vendor);

        Product product = new Product();
        product.setActiveStatus(ActiveStatus.INACTIVE);
        product.setCreatedAt(Instant.now());
        product.setProductType(productType);
        product.setVendor(vendor);
        product.setPrice(new BigDecimal("10"));

        product = entityManager.persistAndFlush(product);

        // Act
        productRepository.setActiveStatusByProductId(
                ActiveStatus.ACTIVE,
                product.getId()
        );

        entityManager.clear();

        // Assert
        Product updatedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        assertEquals(ActiveStatus.ACTIVE, updatedProduct.getActiveStatus());
    }
}
