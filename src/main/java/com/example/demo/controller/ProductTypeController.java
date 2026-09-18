package com.example.demo.controller;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.service.impl.ProductTypeServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product_type")
public class ProductTypeController {


    private final ProductTypeServiceImpl productTypeService;

    @Autowired
    public ProductTypeController(ProductTypeServiceImpl productTypeService) {
        this.productTypeService = productTypeService;
    }

    @PostMapping("/save-product-type")
    public ResponseEntity<ProductTypeDTOResponse> save(@Valid @RequestBody ProductTypeDTORequest productTypeDTORequest){
        ProductTypeDTOResponse productTypeDTOResponse = productTypeService.save(productTypeDTORequest);
        return new ResponseEntity<>(productTypeDTOResponse, HttpStatus.CREATED);
    }

    @GetMapping("/get-all-product-type")
    public ResponseEntity<List<ProductTypeDTOResponse>> getAll(){
        return new ResponseEntity<>(productTypeService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get-product-type-by-id/{productTypeId}")
    public ResponseEntity<ProductTypeDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive")Long productTypeId){
        ProductTypeDTOResponse productTypeDTOResponse = productTypeService.getById(productTypeId);
        return new ResponseEntity<>(productTypeDTOResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete-product-type-by-id/{productTypeId}")
    public ResponseEntity delete (@PathVariable @Positive(message = "ID must be positive") Long productTypeId){
        productTypeService.delete(productTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
