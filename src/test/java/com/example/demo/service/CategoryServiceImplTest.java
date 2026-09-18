package com.example.demo.service;

import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.impl.CategoryServiceImpl;
import com.example.demo.service.impl.ProductTypeServiceImpl;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductTypeServiceImpl productTypeService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTOResponse categoryDTOResponse;
    private CategoryDTORequest categoryDTORequest;
    private ProductType productType;
    private Long categoryId = 1L;

    @BeforeEach
    void setUp(){

        categoryId = 1L;

        category = new Category();
        category.setId(1L);
        category.setCategoryName("Dairy");
        category.setDescription("meh");
        category.setActiveStatus(ActiveStatus.ACTIVE);

        categoryDTOResponse = new CategoryDTOResponse();
        categoryDTOResponse.setId(1L);
        categoryDTOResponse.setCategoryName("Dairy");
        categoryDTOResponse.setDescription("meh");
        categoryDTOResponse.setStatus(ActiveStatus.ACTIVE);


        categoryDTORequest = new CategoryDTORequest();
        categoryDTORequest.setCategoryName("Dairy");
        categoryDTORequest.setDescription("meh");

        productType = new ProductType();
        productType.setName("Milk");
        productType.setCategory(category);
        productType.setProductDescription("ok");
        productType.setId(1L);
        productType.setActiveStatus(ActiveStatus.ACTIVE);
    }

    @Test
    void save_shouldSaveCategoryAndReturnCategoryDTOResponse(){

        when(categoryMapper.toEntity(categoryDTORequest)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTOResponse);

        CategoryDTOResponse result = categoryService.save(categoryDTORequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Dairy", result.getCategoryName());
        assertEquals("meh", result.getDescription());
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDTO(category);
        verify(categoryMapper).toEntity(categoryDTORequest);
    }
//______________________________________________________________________________________________________________________
    @Test
    void getAll_shouldReturnAllCategory(){
        List<Category> categoryList = new ArrayList<>();

        categoryList.add(0,category);
        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTOResponse);

        List<CategoryDTOResponse> resultList = categoryService.getAll();

        assertEquals(1L, resultList.get(0).getId());
        assertEquals("Dairy", resultList.get(0).getCategoryName());
        assertEquals("meh", resultList.get(0).getDescription());
        assertEquals(ActiveStatus.ACTIVE, resultList.get(0).getStatus());
        verify(categoryRepository).findAll();
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void getAll_shouldResourceNotFoundException(){
        List<Category> categoryList = new ArrayList<>();

        when(categoryRepository.findAll()).thenReturn(categoryList);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()-> categoryService.getAll());

        assertEquals("No one Category found", exception.getMessage());
        verify(categoryRepository).findAll();
        verifyNoInteractions(categoryMapper);
    }

    //__________________________________________________________________________________________________________________

    @Test
    void getById_shouldReturnCategoryById(){
        Optional<Category> optionalCategory = Optional.of(category);
        when(categoryRepository.findById(categoryId)).thenReturn(optionalCategory);
        when(categoryMapper.toDTO(optionalCategory.get())).thenReturn(categoryDTOResponse);

        CategoryDTOResponse result = categoryService.getById(categoryId);
        assertEquals(1L, result.getId() );
        assertEquals("Dairy", result.getCategoryName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toDTO(optionalCategory.get());
    }

    @Test
    void getById_shouldResourceNotFoundException_WhenCategoryIsEmpty(){
        Optional<Category> optionalCategory = Optional.empty();
        when(categoryRepository.findById(categoryId)).thenReturn(optionalCategory);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()->  categoryService.getById(categoryId));

        assertEquals("Category not found by Id", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(categoryMapper);
    }
//______________________________________________________________________________________________________________________
    @Test
    void delete_shouldDeleteCategory(){

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productTypeService.existsByCategoryId(categoryId)).thenReturn(false);

        categoryService.delete(categoryId);

        verify(categoryRepository).existsById(categoryId);
        verify(productTypeService).existsByCategoryId(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void delete_shouldResourceNotFoundException(){

        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,()-> categoryService.delete(categoryId));

        assertEquals("Already doesn't exist", exception.getMessage());
        verify(categoryRepository).existsById(categoryId);
        verifyNoInteractions(productTypeService);
        verify(categoryRepository, never()).deleteById(categoryId);
    }

    @Test
    void delete_shouldEntityInUseException(){
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productTypeService.existsByCategoryId(categoryId)).thenReturn(true);
        EntityInUseException exception = assertThrows(EntityInUseException.class,()-> categoryService.delete(categoryId));


        assertEquals("Product type dependencies exist", exception.getMessage());
        verify(categoryRepository).existsById(categoryId);
        verify(productTypeService).existsByCategoryId(categoryId);
        verify(categoryRepository, never()).deleteById(categoryId);
    }
}
