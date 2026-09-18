package com.example.demo.mapper;

import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Batch;
import com.example.demo.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BatchMapperTest {

    private BatchMapper batchMapper;

    private Batch batch;
    private BatchDTORequest batchDTORequest;
    private Instant expirationDate;
    private Instant createdAt;

    @BeforeEach
    void setUp() {
        batchMapper = new BatchMapper();

        expirationDate = Instant.parse("2024-06-01T00:00:00Z");
        createdAt = Instant.parse("2024-01-15T10:00:00Z");

        Product product = new Product();
        product.setId(99L);

        batch = new Batch();
        batch.setId(10L);
        batch.setQuantity(5L);
        batch.setPurchasePrice(new BigDecimal("12.50"));
        batch.setExpirationDate(expirationDate);
        batch.setActiveStatus(ActiveStatus.ACTIVE);
        batch.setCreatedAt(createdAt);
        batch.setProduct(product);

        batchDTORequest = new BatchDTORequest();
        batchDTORequest.setQuantity(5L);
        batchDTORequest.setPurchasePrice(new BigDecimal("12.50"));
        batchDTORequest.setExpirationDate(expirationDate);
        batchDTORequest.setProductId(99L);
    }

    @Test
    void toDTO_shouldMapBatchToDTOResponse() {

        BatchDTOResponse result = batchMapper.toDTO(batch);

        assertEquals(10L, result.getId());
        assertEquals(5L, result.getQuantity());
        assertEquals(new BigDecimal("12.50"), result.getPurchasePrice());
        assertEquals(expirationDate, result.getExpirationDate());
        assertEquals(ActiveStatus.ACTIVE, result.getActiveStatus());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(99L, result.getProductId());
    }

    @Test
    void toEntity_shouldMapDTORequestToBatch() {

        Batch result = batchMapper.toEntity(batchDTORequest);

        assertEquals(5L, result.getQuantity());
        assertEquals(new BigDecimal("12.50"), result.getPurchasePrice());
        assertEquals(expirationDate, result.getExpirationDate());
        assertNull(result.getProduct());
    }
}
