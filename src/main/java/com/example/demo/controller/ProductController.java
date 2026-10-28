package com.example.demo.controller;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductUpdateDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.service.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<ProductDTOResponse> save(@Valid @RequestBody ProductDTORequest productDTO) {
        return new ResponseEntity<>(productService.save(productDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-all-product")
    public ResponseEntity<List<ProductDTOResponse>> getAll() {
        return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-product-by-id/{productId}")
    public ResponseEntity<ProductDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long productId) {
        ProductDTOResponse productDTOResponse = productService.getById(productId);
        return new ResponseEntity<>(productDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @PatchMapping("/update-product/{productId}")
    public ResponseEntity<ProductDTOResponse> update(@PathVariable @Positive(message = "Can't be negative") Long productId, @Valid @RequestBody ProductUpdateDTORequest updateDTORequest) {

        ProductDTOResponse productDTOResponse = productService.updateProduct(productId, updateDTORequest);
        return new ResponseEntity<>(productDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hard-delete/{productId}")
    public ResponseEntity hardDelete(@PathVariable @Positive(message = "ID must be positive") Long productId) {
        productService.hardDelete(productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/soft-delete/{productId}")
    public ResponseEntity softDelete(@PathVariable @Positive(message = "ID must be positive") Long productId) {
        productService.softDelete(productId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER' , 'ADMIN', 'STAFF')")
    @GetMapping("/get-product-filter")
    public List<ProductFilterDTOResponse> getProductFilter() {
        List<ProductFilterDTOResponse> productFilterDTOResponseList = productService.getProductFilter();
        return productFilterDTOResponseList;
    }


    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @PostMapping(value = "/upload-product-image/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTOResponse> uploadProductImage(
            @PathVariable
            @Positive(message = "ID must be positive")
            Long productId,
            @RequestParam("file") MultipartFile file) {

        ProductDTOResponse productDTOResponse =
                productService.uploadProductImage(productId, file);

        return new ResponseEntity<>(productDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @DeleteMapping("/delete-product-image/{productId}")
    public ResponseEntity<ProductDTOResponse> deleteProductImage(
            @PathVariable
            @Positive(message = "ID must be positive")
            Long productId) {

        ProductDTOResponse productDTOResponse =
                productService.deleteProductImage(productId);

        return new ResponseEntity<>(productDTOResponse, HttpStatus.OK);
    }


}
