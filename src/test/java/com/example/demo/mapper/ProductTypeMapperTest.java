package com.example.demo.mapper;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductTypeMapperTest {

    private ProductTypeMapper productTypeMapper;

    private ProductType productType;
    private ProductTypeDTORequest productTypeDTORequest;
    private ProductTypeDTOResponse productTypeDTOResponse;

    @BeforeEach
    void setUp() {
        productTypeMapper = new ProductTypeMapper();

        Category category = new Category();
        category.setId(3L);
        category.setCategoryName("Dairy");

        productType = new ProductType();
        productType.setId(1L);
        productType.setName("Milk");
        productType.setProductDescription("Dairy stuff");
        productType.setCategory(category);
        productType.setActiveStatus(ActiveStatus.ACTIVE);

        productTypeDTORequest = new ProductTypeDTORequest();
        productTypeDTORequest.setName("Milk");
        productTypeDTORequest.setDescription("Dairy stuff");
        productTypeDTORequest.setCategoryId(3L);

        productTypeDTOResponse = new ProductTypeDTOResponse();
        productTypeDTOResponse.setName("Milk");
        productTypeDTOResponse.setDescription("Dairy stuff");
    }

    @Test
    void toDTO_shouldMapProductTypeToDTOResponse() {

        ProductTypeDTOResponse result = productTypeMapper.toDTO(productType);

        assertEquals(1L, result.getId());
        assertEquals("Milk", result.getName());
        assertEquals("Dairy stuff", result.getDescription());
        assertEquals(3L, result.getCategoryId());
        assertEquals(ActiveStatus.ACTIVE, result.getStatus());
    }

    @Test
    void toEntity_shouldMapDTORequestToProductType() {

        ProductType result = productTypeMapper.toEntity(productTypeDTORequest);

        assertEquals("Milk", result.getName());
        assertEquals("Dairy stuff", result.getProductDescription());
        assertNull(result.getCategory());
        assertNull(result.getId());
    }

    @Test
    void toEntity_shouldMapDTOResponseToProductType() {

        ProductType result = productTypeMapper.toEntity(productTypeDTOResponse);

        assertEquals("Milk", result.getName());
        assertEquals("Dairy stuff", result.getProductDescription());
        assertNull(result.getCategory());
    }
}
