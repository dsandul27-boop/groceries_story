package com.example.demo.service.impl;

import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Batch;
import com.example.demo.entities.Product;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BatchMapper;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.BatchService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final BatchMapper batchMapper;
    private final ProductRepository productRepository;

    @Autowired
    public BatchServiceImpl(BatchRepository batchRepository, BatchMapper batchMapper, ProductRepository productRepository) {
        this.batchRepository = batchRepository;
        this.batchMapper = batchMapper;
        this.productRepository = productRepository;
    }


    @Override
    @Transactional
    public BatchDTOResponse save(BatchDTORequest batchDTORequest) {
        Optional<Product> product = productRepository.findById(batchDTORequest.getProductId());
        if (product.isEmpty()){
            throw new ResourceNotFoundException("Product not found");
        }
        Batch batch = batchMapper.toEntity(batchDTORequest);
        batch.setActiveStatus(ActiveStatus.ACTIVE);
        batch.setCreatedAt(Instant.now());
        batch.setProduct(product.get());
        BatchDTOResponse batchDTOResponse = batchMapper.toDTO(batchRepository.save(batch));
        if (product.get().getActiveStatus().equals(ActiveStatus.INACTIVE)){
            productRepository.setActiveStatusByProductId(ActiveStatus.ACTIVE, product.get().getId());
        }
        return batchDTOResponse;
    }

    @Override
    public List<BatchDTOResponse> getAll() {
        List<Batch> batchList = batchRepository.findAll();
        List<BatchDTOResponse> batchDTOResponseList = new ArrayList<>();
        if (batchList.isEmpty()){
            throw new ResourceNotFoundException("Batch not found");
        }
        for (Batch batch : batchList){
            batchDTOResponseList.add(batchMapper.toDTO(batch));
        }
        return batchDTOResponseList ;
    }

    @Override
    public BatchDTOResponse getById(Long id) {
        Optional<Batch> optionalBatch = batchRepository.findById(id);
        if (optionalBatch.isEmpty()){
            throw new ResourceNotFoundException("Batch with id <" + id + "> was not found");
        }

        return batchMapper.toDTO(optionalBatch.get());
    }

    @Override
    public void delete(Long batchId) {
    batchRepository.deleteById(batchId);
    }
}
