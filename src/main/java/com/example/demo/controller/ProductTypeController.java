package com.example.demo.controller;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.service.impl.ProductTypeServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-type")
public class ProductTypeController {


    private final ProductTypeServiceImpl productTypeService;

    @Autowired
    public ProductTypeController(ProductTypeServiceImpl productTypeService) {
        this.productTypeService = productTypeService;
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ProductTypeDTOResponse> save(@Valid @RequestBody ProductTypeDTORequest productTypeDTORequest) {
        ProductTypeDTOResponse productTypeDTOResponse = productTypeService.save(productTypeDTORequest);
        return new ResponseEntity<>(productTypeDTOResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-all-product-type")
    public ResponseEntity<List<ProductTypeDTOResponse>> getAll() {
        return new ResponseEntity<>(productTypeService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/{productTypeId}")
    public ResponseEntity<ProductTypeDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long productTypeId) {
        ProductTypeDTOResponse productTypeDTOResponse = productTypeService.getById(productTypeId);
        return new ResponseEntity<>(productTypeDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hard-delete/{productTypeId}")
    public ResponseEntity hardDelete(@PathVariable @Positive(message = "ID must be positive") Long productTypeId) {
        productTypeService.hardDelete(productTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/soft-delete/{productTypeId}")
    public ResponseEntity softDelete (@PathVariable @Positive(message = "ID must be positive") Long productTypeId){
        productTypeService.softDelete(productTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
