package com.example.demo.controller;

import com.example.demo.dto.request.BatchDTORequest;
import com.example.demo.dto.response.BatchDTOResponse;
import com.example.demo.entities.Batch;
import com.example.demo.service.impl.BatchServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final BatchServiceImpl batchService;

    @Autowired
    public BatchController(BatchServiceImpl batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/save-batch")
    public ResponseEntity<BatchDTOResponse> save(@Valid @RequestBody BatchDTORequest batchDTORequest){
        return new ResponseEntity<>(batchService.save(batchDTORequest), HttpStatus.CREATED);
    }

    @GetMapping("/get-all-batch")
    public ResponseEntity<List<BatchDTOResponse>> getAll(){
        List<BatchDTOResponse> batchList = batchService.getAll();
        return new ResponseEntity<>(batchList, HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-batch-by-id/{batchId}")
    public ResponseEntity<BatchDTOResponse> getById (@PathVariable @Positive(message = "Id must be positive") Long batchId){
        BatchDTOResponse batchDTOResponse = batchService.getById(batchId);
        return new ResponseEntity<>(batchDTOResponse, HttpStatus.OK) ;
    }

    @DeleteMapping("/delete-batch-by-id/{batchId}")
    public ResponseEntity delete (@PathVariable @Positive(message = "Id must be positive") Long batchId){
        batchService.delete(batchId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
