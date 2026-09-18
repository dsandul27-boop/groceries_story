package com.example.demo.mapper;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class CategoryMapperTest {

    @Mock
    private CategoryMapper categoryMapper;

    private Category category;
    private CategoryDTOResponse categoryDTOResponse;
    private CategoryDTORequest categoryDTORequest;


    @BeforeEach
    void setUp(){

        categoryMapper = new CategoryMapper();
        category = new Category();
        category.setId(1L);
        category.setCategoryName("Dairy");
        category.setDescription("meh");
        category.setActiveStatus(ActiveStatus.ACTIVE);

        categoryDTORequest = new CategoryDTORequest();
        categoryDTORequest.setCategoryName("Dairy");
        categoryDTORequest.setDescription("meh");

        categoryDTOResponse = new CategoryDTOResponse();
        categoryDTOResponse.setId(1L);
        categoryDTOResponse.setCategoryName("Dairy");
        categoryDTOResponse.setDescription("meh");
        categoryDTOResponse.setStatus(ActiveStatus.ACTIVE);
    }

    @Test
    void toDTO_ShouldReturnCategoryDTOResponse(){

        CategoryDTOResponse result = categoryMapper.toDTO(category);

        assertEquals(1l, result.getId());
        assertEquals("Dairy", result.getCategoryName());
        assertEquals("meh", result.getDescription());
        assertEquals(ActiveStatus.ACTIVE, result.getStatus());
    }

    @Test
    void toEntity_shouldReturnCategoryEntityByDTORequest(){

        Category result = categoryMapper.toEntity(categoryDTORequest);

        assertEquals(null, result.getId());
        assertEquals("Dairy", result.getCategoryName());
        assertEquals("meh", result.getDescription());
    }

    @Test
    void toEntity_shouldReturnCategoryByDTOResponse(){

        Category result = categoryMapper.toEntity(categoryDTOResponse);

        assertEquals(1l, result.getId());
        assertEquals("Dairy", result.getCategoryName());
        assertEquals("meh", result.getDescription());
        assertEquals(ActiveStatus.ACTIVE, result.getActiveStatus());

    }
}
