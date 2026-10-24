package com.example.demo.repositoy;

import com.example.demo.entities.*;
import com.example.demo.repository.BatchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BatchRepositoryTest {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void sumQuantityByProductId() {

        // Arrange
        Category category = new Category();
        category.setCategoryName("Lactate");
        category.setActiveStatus(ActiveStatus.ACTIVE);
        category = entityManager.persistAndFlush(category);

        ProductType productType = new ProductType();
        productType.setStatus(ActiveStatus.ACTIVE);
        productType.setName("Milk");
        productType.setCategory(category);
        productType = entityManager.persistAndFlush(productType);

        Vendor vendor = new Vendor();
        vendor.setActiveStatus(ActiveStatus.ACTIVE);
        vendor.setName("Latti");
        vendor = entityManager.persistAndFlush(vendor);

        Product product = new Product();
        product.setName("Milk");
        product.setDescription("White milk");
        product.setActiveStatus(ActiveStatus.INACTIVE);
        product.setCreatedAt(Instant.now());
        product.setProductType(productType);
        product.setVendor(vendor);
        product.setPrice(new BigDecimal("10"));
        product = entityManager.persistAndFlush(product);

        Batch batch = new Batch();
        batch.setProduct(product);
        batch.setQuantity(50L);
        batch.setPurchasePrice(new BigDecimal("10.5"));
        batch.setCreatedAt(Instant.now());
        batch.setActiveStatus(ActiveStatus.ACTIVE);
        batch.setExpirationDate(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        entityManager.persistAndFlush(batch);

        // Act
        Long result = batchRepository.sumQuantityByProductId(product.getId());

        // Assert
        assertEquals(50L, result);
    }
}
