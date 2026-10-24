package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.IsInactiveException;
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
import org.springframework.security.test.context.support.WithMockUser;

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
        productType.setStatus(ActiveStatus.ACTIVE);

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
        savedProduct.setActiveStatus(ActiveStatus.ACTIVE);

        productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(productId);
        productDTOResponse.setVendorId(vendorId);
        productDTOResponse.setProductTypeId(productTypeId);
        productDTOResponse.setPrice(new BigDecimal("10"));
        productDTOResponse.setStatus(ActiveStatus.ACTIVE);
    }
    // ... continuarea clasei tale după getById_shouldReturnProductById

    @Test
    void shouldSaveProductWithOwnNameAndDescription_WhenDataIsValid() {
        // Arrange
        productDTORequest.setName("Special Latte");
        productDTORequest.setDescription("Artisan coffee blend with oat milk");

        product.setName("Special Latte");
        product.setDescription("Artisan coffee blend with oat milk");

        savedProduct.setName("Special Latte");
        savedProduct.setDescription("Artisan coffee blend with oat milk");

        productDTOResponse.setName("Special Latte");
        productDTOResponse.setDescription("Artisan coffee blend with oat milk");

        // Setăm un nume diferit pe ProductType pentru a demonstra independența valorilor din cerință
        productType.setName("Dairy Category");

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.of(productType));
        when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);

        // Act
        ProductDTOResponse result = productService.save(productDTORequest);

        // Assert
        assertNotNull(result);
        assertEquals("Special Latte", result.getName());
        assertEquals("Artisan coffee blend with oat milk", result.getDescription());
        // Verificăm regula de business: numele produsului este independent de numele tipului de produs
        assertNotEquals(productType.getName(), result.getName());

        verify(productRepository).save(product);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void shouldSaveProductWithoutDescription_WhenDescriptionIsMissing() {
        // Arrange
        productDTORequest.setName("Latte");
        productDTORequest.setDescription(null); // Description este opțional

        product.setName("Latte");
        product.setDescription(null);

        savedProduct.setName("Latte");
        savedProduct.setDescription(null);

        productDTOResponse.setName("Latte");
        productDTOResponse.setDescription(null);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.of(productType));
        when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);

        // Act
        ProductDTOResponse result = productService.save(productDTORequest);

        // Assert
        assertNotNull(result);
        assertEquals("Latte", result.getName());
        assertNull(result.getDescription());

        verify(productRepository).save(product);
    }

    @Test
    void shouldReturnResponseDtoWithProductOwnFields_WhenMapping() {
        // Arrange
        Product entityProduct = new Product();
        entityProduct.setId(productId);
        entityProduct.setName("Actual Product Name");
        entityProduct.setDescription("Actual Product Description");

        ProductType type = new ProductType();
        type.setName("Fruits Category");
        entityProduct.setProductType(type);

        ProductDTOResponse responseDto = new ProductDTOResponse();
        responseDto.setId(productId);
        responseDto.setName("Actual Product Name");
        responseDto.setDescription("Actual Product Description");

        when(productMapper.toDTO(entityProduct)).thenReturn(responseDto);

        // Act
        ProductDTOResponse result = productMapper.toDTO(entityProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Actual Product Name", result.getName());
        assertEquals("Actual Product Description", result.getDescription());
        assertNotEquals(entityProduct.getProductType().getName(), result.getName());
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
    void getAll_shouldIncludeInactiveAndActiveProducts_AC3() {
        Product activeProd = new Product();
        activeProd.setId(1L);
        activeProd.setActiveStatus(ActiveStatus.ACTIVE);

        Product inactiveProd = new Product();
        inactiveProd.setId(2L);
        inactiveProd.setActiveStatus(ActiveStatus.INACTIVE);

        when(productRepository.findAll()).thenReturn(List.of(activeProd, inactiveProd));
        when(productMapper.toDTO(activeProd)).thenReturn(new ProductDTOResponse());
        when(productMapper.toDTO(inactiveProd)).thenReturn(new ProductDTOResponse());
        List<ProductDTOResponse> result = productService.getAll();

        assertEquals(2, result.size());
        verify(productMapper).toDTO(inactiveProd);
        verify(productMapper).toDTO(activeProd);
    }

    @Test
    void getProductFilter_shouldReturnProducts_AC3() {
        Product activeProd = new Product();
        activeProd.setId(1L);
        activeProd.setActiveStatus(ActiveStatus.ACTIVE);

        Product inactiveProd = new Product();
        inactiveProd.setId(2L);
        inactiveProd.setActiveStatus(ActiveStatus.INACTIVE);

        when(productRepository.findAll()).thenReturn(List.of(activeProd, inactiveProd));
        when(productMapper.toFilterDTO(activeProd)).thenReturn(new ProductFilterDTOResponse());
        when(productMapper.toFilterDTO(inactiveProd)).thenReturn(new ProductFilterDTOResponse());
        List<ProductFilterDTOResponse> result = productService.getProductFilter();

        assertEquals(2, result.size());
        verify(productMapper).toFilterDTO(inactiveProd);
        verify(productMapper).toFilterDTO(activeProd);
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
        productDTOResponse.setQuantity(50L);
        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);
        when(batchRepository.sumQuantityByProductId(productId)).thenReturn(50L);

        ProductDTOResponse result = productService.getById(productId);

        assertEquals(productId, result.getId());
        assertEquals(vendorId, result.getVendorId());
        assertEquals(50L, result.getQuantity());
        verify(productRepository).findById(productId);
        verify(batchRepository).sumQuantityByProductId(productId);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getById(productId));

        assertEquals("Product was not found", exception.getMessage());
        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper, batchRepository);
    }

    //______________________________________________________________________________________________________________________
    // --- SOFT DELETE TESTS ---

    @Test
    void softDelete_shouldFlipStatusToInactive() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));

        productService.softDelete(productId);

        assertEquals(ActiveStatus.INACTIVE, savedProduct.getActiveStatus());
        verify(productRepository).findById(productId);
        verify(productRepository).save(savedProduct);
    }

    @Test
    void softDelete_shouldThrowResourceNotFoundException_whenDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.softDelete(productId));
        assertEquals("Doesn't exist", exception.getMessage());
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void softDelete_shouldThrowIsInactiveException_whenAlreadyInactive() {
        savedProduct.setActiveStatus(ActiveStatus.INACTIVE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
        IsInactiveException exception = assertThrows(IsInactiveException.class,
                () -> productService.softDelete(productId));
        assertEquals("Product is already inactive", exception.getMessage());
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    // --- HARD DELETE TESTS ---
    @Test
    void hardDelete_shouldPhysicallyRemoveRow_whenExistsAndNoDependencies() {
        when(productRepository.existsById(productId)).thenReturn(true);
        when(batchRepository.existsByProductId(productId)).thenReturn(false);
        productService.hardDelete(productId);
        verify(productRepository).existsById(productId);
        verify(batchRepository).existsByProductId(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void hardDelete_shouldThrowResourceNotFoundException_whenDoesNotExist() {
        when(productRepository.existsById(productId)).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.hardDelete(productId));
        assertEquals("Product already doesn't exist", exception.getMessage());
        verify(productRepository).existsById(productId);
        verifyNoInteractions(batchRepository);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void hardDelete_shouldThrowEntityInUseException_whenBatchDependenciesExist() {
        when(productRepository.existsById(productId)).thenReturn(true);
        when(batchRepository.existsByProductId(productId)).thenReturn(true);
        EntityInUseException exception = assertThrows(EntityInUseException.class,
                () -> productService.hardDelete(productId));
        assertEquals("Exist batch dependencies", exception.getMessage());
        verify(productRepository).existsById(productId);
        verify(batchRepository).existsByProductId(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
