package com.example.demo.service;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryDTOResponse> getAll();
    CategoryDTOResponse getById (Long categoryId);
    Category save(CategoryDTORequest categoryDTORequest);
    void delete (Long categoryId);
}
