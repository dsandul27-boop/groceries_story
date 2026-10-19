package com.example.demo.service;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.dto.response.CategoryFilterDTOResponse;
import com.example.demo.entities.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryDTOResponse> getAll();

    CategoryDTOResponse getById (Long categoryId);

    CategoryDTOResponse save(CategoryDTORequest categoryDTORequest);

    void hardDelete (Long categoryId);

    void softDelete(Long categoryId);

    List<CategoryFilterDTOResponse> getCategoryWithProductType();

    List<Category> activeCategory(List<Category> categoryList);

}
