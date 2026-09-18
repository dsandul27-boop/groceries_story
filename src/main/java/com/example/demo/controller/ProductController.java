package com.example.demo.controller;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.service.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping("/save-product")
    public ResponseEntity<ProductDTOResponse> save (@Valid @RequestBody ProductDTORequest productDTO){
        return new ResponseEntity<>(productService.save(productDTO), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-all-product")
    public ResponseEntity<List<ProductDTOResponse>> getAll (){
        return new ResponseEntity<>(productService.getAll(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-product-by-id/{productId}")
    public ResponseEntity<ProductDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long productId){
        ProductDTOResponse productDTOResponse = productService.getById(productId);
        return new ResponseEntity<>(productDTOResponse, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete-by-id/{productId}")
    public ResponseEntity delete (@PathVariable @Positive(message = "ID must be positive") Long productId){
        productService.delete(productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
