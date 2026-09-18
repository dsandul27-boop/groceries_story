package com.example.demo.mapper;

import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.Batch;
import org.springframework.stereotype.Component;

@Component
public class BatchMapper {

    public BatchDTOResponse toDTO(Batch batch) {
        BatchDTOResponse batchDTOResponse = new BatchDTOResponse();
        batchDTOResponse.setId(batch.getId());
        batchDTOResponse.setPurchasePrice(batch.getPurchasePrice());
        batchDTOResponse.setQuantity(batch.getQuantity());
        batchDTOResponse.setExpirationDate(batch.getExpirationDate());
        batchDTOResponse.setActiveStatus(batch.getActiveStatus());
        batchDTOResponse.setCreatedAt(batch.getCreatedAt());
        batchDTOResponse.setProductId(batch.getProduct().getId());

        return batchDTOResponse;
    }

    public Batch toEntity (BatchDTORequest batchDTORequest) {
        Batch batch = new Batch();
        batch.setQuantity(batchDTORequest.getQuantity());
        batch.setPurchasePrice(batchDTORequest.getPurchasePrice());
        batch.setExpirationDate(batchDTORequest.getExpirationDate());
        return batch;
    }
}
