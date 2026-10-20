package com.example.demo.service.impl;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.dto.response.CategoryFilterDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.IsInactiveException;
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
    public CategoryDTOResponse save(CategoryDTORequest categoryDTORequest) {
        Category category = categoryMapper.toEntity(categoryDTORequest);
        category.setActiveStatus(ActiveStatus.ACTIVE);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDTOResponse> getAll() {
        List<Category> categoryList = categoryRepository.findAll();
        if (categoryList.isEmpty()) {
            throw new ResourceNotFoundException("No one Category found");
        }
        List<Category> activeCategory = activeCategory(categoryList);
        List<CategoryDTOResponse> categoryDTOResponseList = new ArrayList<>();
        for (Category category : activeCategory) {
            categoryDTOResponseList.add(categoryMapper.toDTO(category));
        }
        return categoryDTOResponseList;
    }

    @Override
    public CategoryDTOResponse getById(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category not found by Id");
        }

        return categoryMapper.toDTO(optionalCategory.get());
    }


    @Override
    public void hardDelete(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Already doesn't exist");
        }
        boolean productTypeExist = productTypeService.existsByCategoryId(categoryId);
        if (productTypeExist) {
            throw new EntityInUseException("Product type dependencies exist");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public void softDelete(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new ResourceNotFoundException("Doesn't exist");
        }
        Category category = optionalCategory.get();
        if (category.getActiveStatus().equals(ActiveStatus.INACTIVE)) {
            throw new IsInactiveException("Category is already inactive");
        }
        category.setActiveStatus(ActiveStatus.INACTIVE);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryFilterDTOResponse> getCategoryWithProductType() {

        List<Category> categoryList = categoryRepository.findAll();

        if (categoryList.isEmpty()) {
            throw new ResourceNotFoundException("No categories found");
        }

        return activeCategoryFilterList(categoryList);
    }

    private List<Category> activeCategory(List<Category> categoryList) {
        List<Category> activeCategory = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getActiveStatus().equals(ActiveStatus.ACTIVE)) {
                activeCategory.add(category);
            }
        }
        return activeCategory;
    }

    private List<CategoryFilterDTOResponse> activeCategoryFilterList(List<Category> categoryList) {
        List<CategoryFilterDTOResponse> activeCategory = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getActiveStatus().equals(ActiveStatus.ACTIVE)) {
                activeCategory.add(categoryMapper.categoryFilterDTOResponse(category));
            }
        }
        return activeCategory;
    }
}
