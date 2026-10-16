package com.example.demo.controller;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.service.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PostMapping("/save-product")
    public ResponseEntity<ProductDTOResponse> save (@Valid @RequestBody ProductDTORequest productDTO){
        return new ResponseEntity<>(productService.save(productDTO), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-all-product")
    public ResponseEntity<List<ProductDTOResponse>> getAll (){
        return new ResponseEntity<>(productService.getAll(), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-product-by-id/{productId}")
    public ResponseEntity<ProductDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long productId){
        ProductDTOResponse productDTOResponse = productService.getById(productId);
        return new ResponseEntity<>(productDTOResponse, HttpStatus.ACCEPTED);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/delete-by-id/{productId}")
    public ResponseEntity delete (@PathVariable @Positive(message = "ID must be positive") Long productId){
        productService.delete(productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER' , 'ADMIN', 'STAFF')")
    @GetMapping("/get-product-filter")
    public List<ProductFilterDTOResponse> getProductFilter(){
        List<ProductFilterDTOResponse> productFilterDTOResponseList = productService.getProductFilter();
        return  productFilterDTOResponseList;
    }
}
