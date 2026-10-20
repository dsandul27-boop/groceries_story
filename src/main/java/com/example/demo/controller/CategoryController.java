package com.example.demo.controller;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.dto.response.CategoryFilterDTOResponse;
import com.example.demo.service.impl.CategoryServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-all-category")
    public ResponseEntity<List<CategoryDTOResponse>> getAll() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.ACCEPTED);
    }


    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-category-by-id/{id}")
    public ResponseEntity<CategoryDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long id) {
        CategoryDTOResponse categoryDTOResponse = categoryService.getById(id);
        return new ResponseEntity<>(categoryDTOResponse, HttpStatus.ACCEPTED);
    }


    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PostMapping("/save-category")
    public ResponseEntity<CategoryDTOResponse> save(@Valid @RequestBody CategoryDTORequest categoryDTORequest) {
        return new ResponseEntity<>(categoryService.save(categoryDTORequest), HttpStatus.ACCEPTED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hard-delete-category/{categoryId}")
    public ResponseEntity hardDelete(@PathVariable @Positive(message = "ID must be positive") Long categoryId) {
        categoryService.hardDelete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/soft-delete-category/{categoryId}")
    public ResponseEntity softDelete(@PathVariable @Positive(message = "ID must be positive") Long categoryId) {
        categoryService.softDelete(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF', 'ADMIN')")
    @GetMapping("/get-all-category-and-product-type")
    public ResponseEntity<List<CategoryFilterDTOResponse>> getCategory() {
        List<CategoryFilterDTOResponse> categoryFilterDTOResponseList = categoryService.getCategoryWithProductType();
        return new ResponseEntity<>(categoryFilterDTOResponseList, HttpStatus.OK);
    }

}
