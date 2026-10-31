package com.example.demo.repository;

import com.example.demo.dto.request.ProductFilterCriteria;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.repository.spec.ProductSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProductType dairyType;
    private Vendor olympusVendor;

    @BeforeEach
    void setUp() {
        Category freshFood = new Category();
        freshFood.setCategoryName("Fresh Food");
        freshFood.setActiveStatus(ActiveStatus.ACTIVE);
        entityManager.persist(freshFood);

        dairyType = new ProductType();
        dairyType.setName("Dairy");
        dairyType.setCategory(freshFood);

        dairyType.setStatus(ActiveStatus.ACTIVE);
        entityManager.persist(dairyType);

        olympusVendor = new Vendor();
        olympusVendor.setName("Olympus");

        olympusVendor.setActiveStatus(ActiveStatus.ACTIVE);
        entityManager.persist(olympusVendor);

        saveProduct("Lapte UHT 3.5% Active", BigDecimal.valueOf(8.50), ActiveStatus.ACTIVE);
        saveProduct("Iaurt Grecesc Inactive", BigDecimal.valueOf(4.20), ActiveStatus.INACTIVE);
        saveProduct("Telemea de Vacă Active", BigDecimal.valueOf(24.90), ActiveStatus.ACTIVE);

        entityManager.flush();
    }



    private void saveProduct(String name, BigDecimal price, ActiveStatus status) {
        Product p = new Product();
        p.setName(name);
        p.setDescription("Produs alimentar proaspăt");
        p.setPrice(price);
        p.setActiveStatus(status);
        p.setProductType(dairyType);
        p.setVendor(olympusVendor);
        p.setCreatedAt(Instant.now());
        entityManager.persist(p);
    }

    @Test
    void givenActiveOnlyTrue_whenFiltering_thenReturnOnlyActiveProducts() {
        ProductFilterCriteria criteria = new ProductFilterCriteria();
        criteria.setActiveOnly(true);

        Specification<Product> spec = ProductSpecification.matching(criteria);
        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(p -> p.getActiveStatus() == ActiveStatus.ACTIVE);
    }

    @Test
    void givenPriceRange_whenFiltering_thenReturnProductsWithinRange() {
        ProductFilterCriteria criteria = new ProductFilterCriteria();
        criteria.setMinPrice(BigDecimal.valueOf(5.00));
        criteria.setMaxPrice(BigDecimal.valueOf(15.00));

        Specification<Product> spec = ProductSpecification.matching(criteria);
        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Lapte UHT 3.5% Active");
    }

    @Test
    void givenNoSort_whenQuerying_thenSortActiveFirstThenByNameAscending() {
        ProductFilterCriteria criteria = new ProductFilterCriteria();
        Specification<Product> spec = ProductSpecification.matching(criteria);

        Sort defaultSort = Sort.by("activeStatus").ascending().and(Sort.by("name").ascending());
        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 10, defaultSort));

        List<Product> content = result.getContent();
        assertThat(content).hasSize(3);

        assertThat(content.get(0).getName()).isEqualTo("Lapte UHT 3.5% Active");
        assertThat(content.get(1).getName()).isEqualTo("Telemea de Vacă Active");
        assertThat(content.get(2).getName()).isEqualTo("Iaurt Grecesc Inactive");
    }

    @Test
    void givenUnknownVendorId_whenFiltering_thenReturnEmptyPage() {
        ProductFilterCriteria criteria = new ProductFilterCriteria();
        criteria.setVendorId(999L);

        Specification<Product> spec = ProductSpecification.matching(criteria);
        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }
}
