package com.example.demo.mapper;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductMapperTest {

    private ProductMapper productMapper;

    private Product product;
    private ProductDTORequest productDTORequest;
    private Instant createdAt;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        Vendor vendor = new Vendor();
        vendor.setId(5L);
        vendor.setName("Latti");

        ProductType productType = new ProductType();
        productType.setId(7L);
        productType.setName("Milk");

        createdAt = Instant.parse("2024-01-15T10:00:00Z");

        product = new Product();
        product.setId(1L);
        product.setVendor(vendor);
        product.setProductType(productType);
        product.setPrice(new BigDecimal("10"));
        product.setActiveStatus(ActiveStatus.ACTIVE);
        product.setCreatedAt(createdAt);

        productDTORequest = new ProductDTORequest();
        productDTORequest.setVendorId(5L);
        productDTORequest.setProductTypeId(7L);
        productDTORequest.setPrice(new BigDecimal("10"));
    }

    @Test
    void toDTO_shouldMapProductToDTOResponse() {

        ProductDTOResponse result = productMapper.toDTO(product);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getVendorId());
        assertEquals(7L, result.getProductTypeId());
        assertEquals(new BigDecimal("10"), result.getPrice());
        assertEquals(ActiveStatus.ACTIVE, result.getActiveStatus());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void toEntity_shouldMapDTORequestToProduct() {

        Product result = productMapper.toEntity(productDTORequest);

        assertEquals(new BigDecimal("10"), result.getPrice());
        assertNull(result.getVendor());
        assertNull(result.getProductType());
        assertNull(result.getActiveStatus());
    }
}
