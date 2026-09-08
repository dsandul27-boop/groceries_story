package com.example.demo.service.impl;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductTypeServiceImpl productTypeService;


    @Autowired
    public CategoryServiceImpl(CategoryRepository cagetoryRepository, CategoryMapper categoryMapper, ProductTypeServiceImpl productTypeService) {
        this.categoryRepository = cagetoryRepository;
        this.categoryMapper = categoryMapper;
        this.productTypeService = productTypeService;
    }

    @Override
    public List<CategoryDTOResponse> getAll() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDTOResponse> categoryDTOResponseList = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            categoryDTOResponseList.add(categoryMapper.toDTO(categoryList.get(i)));
        }
        return categoryDTOResponseList;
    }

    @Override
    public CategoryDTOResponse getById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDTO(category);
    }

    @Override
    public Category save(CategoryDTORequest categoryDTORequest) {
        return categoryRepository.save(categoryMapper.toEntity(categoryDTORequest));

    }

    @Override
    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
