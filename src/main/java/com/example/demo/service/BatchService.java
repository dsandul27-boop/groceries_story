package com.example.demo.service;

import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.Batch;

import java.util.List;

public interface BatchService {
    BatchDTOResponse save(BatchDTORequest batchDTORequest);
    List<BatchDTOResponse> getAll();
    BatchDTOResponse getById(Long id);
    void delete(Long id);
}
