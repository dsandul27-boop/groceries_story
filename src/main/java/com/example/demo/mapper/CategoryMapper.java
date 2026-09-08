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
        return categoryDTOResponse;
    }

    public Category toEntity(CategoryDTORequest categoryDTORequest){
        Category category = new Category();
        category.setId(categoryDTORequest.getId());
        category.setCategoryName(categoryDTORequest.getCategoryName());
        category.setDescription(categoryDTORequest.getDescription());
        return category;
    }
}
