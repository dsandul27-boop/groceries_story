package com.example.demo.controller;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.service.impl.CategoryServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/get-all-category")
    public ResponseEntity<List<CategoryDTOResponse>> getAll(){
        List<CategoryDTOResponse> categoryDTOResponseList = categoryService.getAll();
        return new ResponseEntity<>(categoryDTOResponseList, HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-category-by-id/{id}")
    public ResponseEntity<CategoryDTOResponse> getById(
            @PathVariable
            @Positive(message = "ID must be positive") Long id){
       CategoryDTOResponse categoryDTOResponse= categoryService.getById(id);
       return new ResponseEntity<>(categoryDTOResponse, HttpStatus.ACCEPTED);
    }

    @PostMapping("/save-category")
    public ResponseEntity<CategoryDTOResponse> save(@Valid @RequestBody CategoryDTORequest categoryDTORequest){
         return new ResponseEntity<>(categoryService.save(categoryDTORequest), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity delete(@PathVariable @Positive(message = "ID must be positive") Long categoryId){
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
