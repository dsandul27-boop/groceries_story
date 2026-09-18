package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Vendor vendor;
    private ProductType productType;
    private Product product;
    private Product savedProduct;
    private ProductDTORequest productDTORequest;
    private ProductDTOResponse productDTOResponse;
    private Long vendorId = 1L;
    private Long productTypeId = 1L;
    private Long productId = 1L;

    @BeforeEach
    void setUp() {

        vendorId = 1L;
        productTypeId = 1L;
        productId = 1L;

        vendor = new Vendor();
        vendor.setId(vendorId);
        vendor.setName("Latti");
        vendor.setActiveStatus(ActiveStatus.ACTIVE);

        productType = new ProductType();
        productType.setId(productTypeId);
        productType.setName("Milk");
        productType.setActiveStatus(ActiveStatus.ACTIVE);

        productDTORequest = new ProductDTORequest();
        productDTORequest.setVendorId(vendorId);
        productDTORequest.setProductTypeId(productTypeId);
        productDTORequest.setPrice(new BigDecimal("10"));

        product = new Product();
        product.setPrice(new BigDecimal("10"));

        savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setVendor(vendor);
        savedProduct.setProductType(productType);
        savedProduct.setPrice(new BigDecimal("10"));
        savedProduct.setActiveStatus(ActiveStatus.INACTIVE);

        productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(productId);
        productDTOResponse.setVendorId(vendorId);
        productDTOResponse.setProductTypeId(productTypeId);
        productDTOResponse.setPrice(new BigDecimal("10"));
        productDTOResponse.setActiveStatus(ActiveStatus.INACTIVE);
    }

    @Test
    void save_shouldSaveProductAndReturnProductDTOResponse() {

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.of(productType));
        when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);

        ProductDTOResponse result = productService.save(productDTORequest);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(vendorId, result.getVendorId());
        assertEquals(productTypeId, result.getProductTypeId());
        assertEquals(ActiveStatus.INACTIVE, result.getActiveStatus());
        verify(vendorRepository).findById(vendorId);
        verify(productTypeRepository).findById(productTypeId);
        verify(productMapper).toEntity(productDTORequest);
        verify(productRepository).save(product);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void save_shouldThrowResourceNotFoundException_whenVendorNotFound() {

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.save(productDTORequest));

        assertEquals("Vendor is empty", exception.getMessage());
        verify(vendorRepository).findById(vendorId);
        verifyNoInteractions(productTypeRepository, productMapper, productRepository);
    }

    @Test
    void save_shouldThrowResourceNotFoundException_whenProductTypeNotFound() {

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.save(productDTORequest));

        assertEquals("Product type is empty", exception.getMessage());
        verify(vendorRepository).findById(vendorId);
        verify(productTypeRepository).findById(productTypeId);
        verifyNoInteractions(productMapper, productRepository);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getAll_shouldReturnAllProducts() {

        List<Product> productList = new ArrayList<>();
        productList.add(savedProduct);

        when(productRepository.findAll()).thenReturn(productList);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);

        List<ProductDTOResponse> resultList = productService.getAll();

        assertEquals(1, resultList.size());
        assertEquals(productId, resultList.get(0).getId());
        assertEquals(vendorId, resultList.get(0).getVendorId());
        verify(productRepository).findAll();
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void getAll_shouldThrowResourceNotFoundException_whenNoProductsExist() {

        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getAll());

        assertEquals("No one product found", exception.getMessage());
        verify(productRepository).findAll();
        verifyNoInteractions(productMapper);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getById_shouldReturnProductById() {

        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);

        ProductDTOResponse result = productService.getById(productId);

        assertEquals(productId, result.getId());
        assertEquals(vendorId, result.getVendorId());
        verify(productRepository).findById(productId);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getById(productId));

        assertEquals("Product was not found", exception.getMessage());
        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void delete_shouldDeleteProduct() {

        when(productRepository.existsById(productId)).thenReturn(true);
        when(batchRepository.existsByProductId(productId)).thenReturn(false);

        productService.delete(productId);

        verify(productRepository).existsById(productId);
        verify(batchRepository).existsByProductId(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {

        when(productRepository.existsById(productId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(productId));

        assertEquals("Product already doesn't exist", exception.getMessage());
        verify(productRepository).existsById(productId);
        verifyNoInteractions(batchRepository);
        verify(productRepository, never()).deleteById(productId);
    }

    @Test
    void delete_shouldThrowEntityInUseException_whenBatchExists() {

        when(productRepository.existsById(productId)).thenReturn(true);
        when(batchRepository.existsByProductId(productId)).thenReturn(true);

        EntityInUseException exception = assertThrows(EntityInUseException.class,
                () -> productService.delete(productId));

        assertEquals("Exist batch dependencies", exception.getMessage());
        verify(productRepository).existsById(productId);
        verify(batchRepository).existsByProductId(productId);
        verify(productRepository, never()).deleteById(productId);
    }
}
