package com.example.demo.mapper;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTOResponse toDTO(Category category){
        CategoryDTOResponse categoryDTOResponse = new CategoryDTOResponse();
        categoryDTOResponse.setId(category.getId());
        categoryDTOResponse.setCategoryName(category.getCategoryName());
        categoryDTOResponse.setDescription(category.getDescription());
        categoryDTOResponse.setStatus(category.getActiveStatus());
        return categoryDTOResponse;
    }

    public Category toEntity(CategoryDTORequest categoryDTORequest){
        Category category = new Category();
        category.setCategoryName(categoryDTORequest.getCategoryName());
        category.setDescription(categoryDTORequest.getDescription());
        return category;
    }

    public Category toEntity(CategoryDTOResponse categoryDTOResponse){
        Category category = new Category();
        category.setId(categoryDTOResponse.getId());
        category.setCategoryName(categoryDTOResponse.getCategoryName());
        category.setDescription(categoryDTOResponse.getDescription());
        category.setActiveStatus(categoryDTOResponse.getStatus());
        return category;
    }
}
