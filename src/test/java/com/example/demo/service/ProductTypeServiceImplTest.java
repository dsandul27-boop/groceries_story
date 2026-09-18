package com.example.demo.service;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductTypeMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.service.impl.ProductTypeServiceImpl;
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
public class ProductTypeServiceImplTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductTypeMapper productTypeMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductTypeServiceImpl productTypeService;

    private Category category;
    private ProductType productType;
    private ProductType savedProductType;
    private ProductTypeDTORequest productTypeDTORequest;
    private ProductTypeDTOResponse productTypeDTOResponse;
    private Long categoryId = 1L;
    private Long productTypeId = 1L;

    @BeforeEach
    void setUp() {

        categoryId = 1L;
        productTypeId = 1L;

        category = new Category();
        category.setId(categoryId);
        category.setCategoryName("Dairy");
        category.setActiveStatus(ActiveStatus.ACTIVE);

        productTypeDTORequest = new ProductTypeDTORequest();
        productTypeDTORequest.setName("Milk");
        productTypeDTORequest.setDescription("Dairy stuff");
        productTypeDTORequest.setCategoryId(categoryId);

        productType = new ProductType();
        productType.setName("Milk");
        productType.setProductDescription("Dairy stuff");

        savedProductType = new ProductType();
        savedProductType.setId(productTypeId);
        savedProductType.setName("Milk");
        savedProductType.setProductDescription("Dairy stuff");
        savedProductType.setCategory(category);
        savedProductType.setActiveStatus(ActiveStatus.ACTIVE);

        productTypeDTOResponse = new ProductTypeDTOResponse();
        productTypeDTOResponse.setId(productTypeId);
        productTypeDTOResponse.setName("Milk");
        productTypeDTOResponse.setDescription("Dairy stuff");
        productTypeDTOResponse.setCategoryId(categoryId);
        productTypeDTOResponse.setStatus(ActiveStatus.ACTIVE);
    }

    @Test
    void save_shouldSaveProductTypeAndReturnDTOResponse() {

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productTypeMapper.toEntity(productTypeDTORequest)).thenReturn(productType);
        when(productTypeRepository.save(productType)).thenReturn(savedProductType);
        when(productTypeMapper.toDTO(savedProductType)).thenReturn(productTypeDTOResponse);

        ProductTypeDTOResponse result = productTypeService.save(productTypeDTORequest);

        assertNotNull(result);
        assertEquals(productTypeId, result.getId());
        assertEquals("Milk", result.getName());
        assertEquals(categoryId, result.getCategoryId());
        assertEquals(ActiveStatus.ACTIVE, result.getStatus());
        verify(categoryRepository).findById(categoryId);
        verify(productTypeMapper).toEntity(productTypeDTORequest);
        verify(productTypeRepository).save(productType);
        verify(productTypeMapper).toDTO(savedProductType);
    }

    @Test
    void save_shouldThrowResourceNotFoundException_whenCategoryNotFound() {

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.save(productTypeDTORequest));

        assertEquals("Category not found by Id", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productTypeMapper, productTypeRepository);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getAll_shouldReturnAllProductTypes() {

        List<ProductType> productTypeList = new ArrayList<>();
        productTypeList.add(savedProductType);

        when(productTypeRepository.findAll()).thenReturn(productTypeList);
        when(productTypeMapper.toDTO(savedProductType)).thenReturn(productTypeDTOResponse);

        List<ProductTypeDTOResponse> resultList = productTypeService.getAll();

        assertEquals(1, resultList.size());
        assertEquals(productTypeId, resultList.get(0).getId());
        assertEquals("Milk", resultList.get(0).getName());
        verify(productTypeRepository).findAll();
        verify(productTypeMapper).toDTO(savedProductType);
    }

    @Test
    void getAll_shouldThrowResourceNotFoundException_whenNoProductTypesExist() {

        when(productTypeRepository.findAll()).thenReturn(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.getAll());

        assertEquals("No product type found", exception.getMessage());
        verify(productTypeRepository).findAll();
        verifyNoInteractions(productTypeMapper);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getById_shouldReturnProductTypeById() {

        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.of(savedProductType));
        when(productTypeMapper.toDTO(savedProductType)).thenReturn(productTypeDTOResponse);

        ProductTypeDTOResponse result = productTypeService.getById(productTypeId);

        assertEquals(productTypeId, result.getId());
        assertEquals("Milk", result.getName());
        verify(productTypeRepository).findById(productTypeId);
        verify(productTypeMapper).toDTO(savedProductType);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenProductTypeDoesNotExist() {

        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.getById(productTypeId));

        assertEquals("Product not found", exception.getMessage());
        verify(productTypeRepository).findById(productTypeId);
        verifyNoInteractions(productTypeMapper);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void delete_shouldDeleteProductType() {

        when(productTypeRepository.existsById(productTypeId)).thenReturn(true);
        when(productRepository.existsByProductTypeId(productTypeId)).thenReturn(false);

        productTypeService.delete(productTypeId);

        verify(productTypeRepository).existsById(productTypeId);
        verify(productRepository).existsByProductTypeId(productTypeId);
        verify(productTypeRepository).deleteById(productTypeId);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenProductTypeDoesNotExist() {

        when(productTypeRepository.existsById(productTypeId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productTypeService.delete(productTypeId));

        assertEquals("Already doesn't exist", exception.getMessage());
        verify(productTypeRepository).existsById(productTypeId);
        verifyNoInteractions(productRepository);
        verify(productTypeRepository, never()).deleteById(productTypeId);
    }

    @Test
    void delete_shouldThrowEntityInUseException_whenProductExists() {

        when(productTypeRepository.existsById(productTypeId)).thenReturn(true);
        when(productRepository.existsByProductTypeId(productTypeId)).thenReturn(true);

        EntityInUseException exception = assertThrows(EntityInUseException.class,
                () -> productTypeService.delete(productTypeId));

        assertEquals("Product dependencies exist", exception.getMessage());
        verify(productTypeRepository).existsById(productTypeId);
        verify(productRepository).existsByProductTypeId(productTypeId);
        verify(productTypeRepository, never()).deleteById(productTypeId);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void existsByCategoryId_shouldDelegateToRepository() {

        when(productTypeRepository.existsByCategoryId(categoryId)).thenReturn(true);

        boolean result = productTypeService.existsByCategoryId(categoryId);

        assertTrue(result);
        verify(productTypeRepository).existsByCategoryId(categoryId);
    }
}
