package com.example.demo.service.impl;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductTypeService productTypeService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, ProductTypeService productTypeService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productTypeService = productTypeService;
    }

    @Override
    public List<CategoryDTOResponse> getAll() {
        List<Category> categoryList = categoryRepository.findAll();
        if(categoryList.isEmpty()){
            throw new ResourceNotFoundException("No one Category found");
        }
        List<CategoryDTOResponse> categoryDTOResponseList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryDTOResponseList.add(categoryMapper.toDTO(category));
        }
        return categoryDTOResponseList;
    }

    @Override
    public CategoryDTOResponse getById(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()){
            throw new ResourceNotFoundException("Category not found by Id");
        }
        return categoryMapper.toDTO(optionalCategory.get());
    }

    @Override
    public CategoryDTOResponse save(CategoryDTORequest categoryDTORequest) {
        Category category = categoryMapper.toEntity(categoryDTORequest);
        category.setActiveStatus(ActiveStatus.ACTIVE);
        return categoryMapper.toDTO(categoryRepository.save(category));

    }

    @Override
    public void delete(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)){
            throw new ResourceNotFoundException("Already doesn't exist");
        }
        boolean productTypeExist = productTypeService.existsByCategoryId(categoryId);
        if (productTypeExist){
            throw new EntityInUseException("Product type dependencies exist");
        }
        categoryRepository.deleteById(categoryId);
    }
}
