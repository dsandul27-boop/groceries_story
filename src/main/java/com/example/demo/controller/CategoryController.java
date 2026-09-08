package com.example.demo.controller;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping()
    public List<CategoryDTOResponse> getAll(){
        List<CategoryDTOResponse> categoryDTOResponseList = categoryService.getAll();
        return categoryDTOResponseList;
    }

    @GetMapping("/get-category-by-id")
    public CategoryDTOResponse getById(Long id){
       CategoryDTOResponse categoryDTOResponse= categoryService.getById(id);
       return  categoryDTOResponse;
    }

    @PostMapping("/save-category")
    public Long save(CategoryDTORequest categoryDTORequest){
        Long id = categoryService.save(categoryDTORequest);
        return  id;
    }

    @DeleteMapping("/delete-category")
    public void delete(Long categoryId){
        categoryService.delete(categoryId);
    }

}
