package com.example.demo.service;

import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Batch;
import com.example.demo.entities.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BatchMapper;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.impl.BatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BatchServiceImplTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchMapper batchMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BatchServiceImpl batchService;

    private Product activeProduct;
    private Batch batch;
    private Batch savedBatch;
    private BatchDTORequest batchDTORequest;
    private BatchDTOResponse batchDTOResponse;
    private Long productId = 1L;
    private Long batchId = 1L;
    private Instant expirationDate;

    @BeforeEach
    void setUp() {

        productId = 1L;
        batchId = 1L;
        expirationDate = Instant.parse("2024-06-01T00:00:00Z");

        activeProduct = new Product();
        activeProduct.setId(productId);
        activeProduct.setActiveStatus(ActiveStatus.ACTIVE);

        batchDTORequest = new BatchDTORequest();
        batchDTORequest.setQuantity(5L);
        batchDTORequest.setPurchasePrice(new BigDecimal("12.50"));
        batchDTORequest.setExpirationDate(expirationDate);
        batchDTORequest.setProductId(productId);

        batch = new Batch();
        batch.setQuantity(5L);
        batch.setPurchasePrice(new BigDecimal("12.50"));
        batch.setExpirationDate(expirationDate);

        savedBatch = new Batch();
        savedBatch.setId(batchId);
        savedBatch.setQuantity(5L);
        savedBatch.setPurchasePrice(new BigDecimal("12.50"));
        savedBatch.setExpirationDate(expirationDate);
        savedBatch.setActiveStatus(ActiveStatus.ACTIVE);
        savedBatch.setProduct(activeProduct);

        batchDTOResponse = new BatchDTOResponse();
        batchDTOResponse.setId(batchId);
        batchDTOResponse.setQuantity(5L);
        batchDTOResponse.setPurchasePrice(new BigDecimal("12.50"));
        batchDTOResponse.setExpirationDate(expirationDate);
        batchDTOResponse.setActiveStatus(ActiveStatus.ACTIVE);
        batchDTOResponse.setProductId(productId);
    }

    @Test
    void save_shouldSaveBatchAndReturnDTOResponse_whenProductIsActive() {

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
        when(batchMapper.toEntity(batchDTORequest)).thenReturn(batch);
        when(batchRepository.save(batch)).thenReturn(savedBatch);
        when(batchMapper.toDTO(savedBatch)).thenReturn(batchDTOResponse);

        BatchDTOResponse result = batchService.save(batchDTORequest);

        assertNotNull(result);
        assertEquals(batchId, result.getId());
        assertEquals(productId, result.getProductId());
        verify(productRepository).findById(productId);
        verify(batchMapper).toEntity(batchDTORequest);
        verify(batchRepository).save(batch);
        verify(batchMapper).toDTO(savedBatch);
        verify(productRepository, never()).setActiveStatusByProductId(any(), any());
    }

    @Test
    void save_shouldReactivateProduct_whenProductIsInactive() {

        Product inactiveProduct = new Product();
        inactiveProduct.setId(productId);
        inactiveProduct.setActiveStatus(ActiveStatus.INACTIVE);

        when(productRepository.findById(productId)).thenReturn(Optional.of(inactiveProduct));
        when(batchMapper.toEntity(batchDTORequest)).thenReturn(batch);
        when(batchRepository.save(batch)).thenReturn(savedBatch);
        when(batchMapper.toDTO(savedBatch)).thenReturn(batchDTOResponse);

        batchService.save(batchDTORequest);

        verify(productRepository).setActiveStatusByProductId(ActiveStatus.ACTIVE, productId);
    }

    @Test
    void save_shouldThrowResourceNotFoundException_whenProductNotFound() {

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> batchService.save(batchDTORequest));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findById(productId);
        verifyNoInteractions(batchMapper, batchRepository);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getAll_shouldReturnAllBatches() {

        List<Batch> batchList = new ArrayList<>();
        batchList.add(savedBatch);

        when(batchRepository.findAll()).thenReturn(batchList);
        when(batchMapper.toDTO(savedBatch)).thenReturn(batchDTOResponse);

        List<BatchDTOResponse> resultList = batchService.getAll();

        assertEquals(1, resultList.size());
        assertEquals(batchId, resultList.get(0).getId());
        verify(batchRepository).findAll();
        verify(batchMapper).toDTO(savedBatch);
    }

    @Test
    void getAll_shouldThrowResourceNotFoundException_whenNoBatchesExist() {

        when(batchRepository.findAll()).thenReturn(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> batchService.getAll());

        assertEquals("Batch not found", exception.getMessage());
        verify(batchRepository).findAll();
        verifyNoInteractions(batchMapper);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getById_shouldReturnBatchById() {

        when(batchRepository.findById(batchId)).thenReturn(Optional.of(savedBatch));
        when(batchMapper.toDTO(savedBatch)).thenReturn(batchDTOResponse);

        BatchDTOResponse result = batchService.getById(batchId);

        assertEquals(batchId, result.getId());
        verify(batchRepository).findById(batchId);
        verify(batchMapper).toDTO(savedBatch);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenBatchDoesNotExist() {

        when(batchRepository.findById(batchId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> batchService.getById(batchId));

        assertEquals("Batch with id <" + batchId + "> was not found", exception.getMessage());
        verify(batchRepository).findById(batchId);
        verifyNoInteractions(batchMapper);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void delete_shouldDeleteBatchById() {

        batchService.delete(batchId);

        verify(batchRepository).deleteById(batchId);
    }
}
