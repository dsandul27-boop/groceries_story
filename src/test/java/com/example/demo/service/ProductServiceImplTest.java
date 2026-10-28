package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductUpdateDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.IsInactiveException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.NotBlankException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.media.MediaStorage;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

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
    private MediaStorage mediaStorage;

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
    private ProductUpdateDTORequest productUpdateDTORequest;
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

        productUpdateDTORequest = new ProductUpdateDTORequest();
        productUpdateDTORequest.setName("Updated Latte");
        productUpdateDTORequest.setDescription("Fresh espresso with steamed milk");
    }

    @Test
    void shouldSaveProductWithOwnNameAndDescription_WhenDataIsValid() {

        productDTORequest.setName("Special Latte");
        productDTORequest.setDescription("Artisan coffee blend with oat milk");

        product.setName("Special Latte");
        product.setDescription("Artisan coffee blend with oat milk");
        savedProduct.setName("Special Latte");
        savedProduct.setDescription("Artisan coffee blend with oat milk");

        productDTOResponse.setName("Special Latte");
        productDTOResponse.setDescription("Artisan coffee blend with oat milk");

        productType.setName("Dairy Category");

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(productTypeRepository.findById(productTypeId)).thenReturn(Optional.of(productType));
        when(productMapper.toEntity(productDTORequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);


        ProductDTOResponse result = productService.save(productDTORequest);


        assertNotNull(result);
        assertEquals("Special Latte", result.getName());
        assertEquals("Artisan coffee blend with oat milk", result.getDescription());

        assertNotEquals(productType.getName(), result.getName());

        verify(productRepository).save(product);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void shouldSaveProductWithoutDescription_WhenDescriptionIsMissing() {

        productDTORequest.setName("Latte");
        productDTORequest.setDescription(null);

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

        ProductDTOResponse result = productService.save(productDTORequest);

        assertNotNull(result);
        assertEquals("Latte", result.getName());
        assertNull(result.getDescription());

        verify(productRepository).save(product);
    }

    @Test
    void shouldReturnResponseDtoWithProductOwnFields_WhenMapping() {
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

        ProductDTOResponse result = productMapper.toDTO(entityProduct);

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

    //TEST FOR UPDATE
    @Test
    void updateProduct_shouldApplyProvidedFields_whenDataIsValid() {
        product.setName("Updated Latte");
        product.setDescription("Fresh espresso with steamed milk");
        savedProduct.setName("Updated Latte");
        savedProduct.setDescription("Fresh espresso with steamed milk");
        productDTOResponse.setName("Updated Latte");
        productDTOResponse.setDescription("Fresh espresso with steamed milk");
        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);
        ProductDTOResponse result = productService.updateProduct(productId, productUpdateDTORequest);
        assertNotNull(result);
        assertEquals("Updated Latte", result.getName());
        assertEquals("Fresh espresso with steamed milk", result.getDescription());
        verify(productRepository).findById(productId);
        verify(productRepository).save(savedProduct);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException_whenIdIsUnknown() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(999L, productUpdateDTORequest));
        assertEquals("Product was not found", exception.getMessage());
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);
    }

    @Test
    void updateProduct_shouldKeepNameUnchanged_whenNameIsAbsent() {
        productUpdateDTORequest.setName(null);
        productUpdateDTORequest.setDescription("New description text");

        savedProduct.setName("Latte");
        savedProduct.setDescription("Old Description");

        productDTOResponse.setName("Latte");
        productDTOResponse.setDescription("New description text");

        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);


        ProductDTOResponse result = productService.updateProduct(productId, productUpdateDTORequest);


        assertNotNull(result);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        assertEquals("Latte", captor.getValue().getName());
        assertEquals("New description text", captor.getValue().getDescription());

        verify(productRepository).findById(productId);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void updateProduct_shouldThrowBadRequest_whenNameIsPresentButBlank() {

        productUpdateDTORequest.setName("   ");
        productUpdateDTORequest.setDescription("Valid description");

        savedProduct.setName("Latte");

        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));

        NotBlankException exception = assertThrows(NotBlankException.class,
                () -> productService.updateProduct(productId, productUpdateDTORequest));

        assertEquals("Missing statement", exception.getMessage());

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);
    }

    @Test
    void updateProduct_shouldKeepDescriptionUnchanged_whenDescriptionIsAbsent() {
        productUpdateDTORequest.setName("New Latte Name");
        productUpdateDTORequest.setDescription(null);

        savedProduct.setName("Latte");
        savedProduct.setDescription("Old Description");

        productDTOResponse.setName("New Latte Name");
        productDTOResponse.setDescription("Old Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTOResponse);

        ProductDTOResponse result = productService.updateProduct(productId, productUpdateDTORequest);

        assertNotNull(result);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        assertEquals("New Latte Name", captor.getValue().getName());
        assertEquals("Old Description", captor.getValue().getDescription());

        verify(productRepository).findById(productId);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    void updateProduct_shouldThrowResourceNotFoundException_whenDescriptionIsPresentButBlank() {
        productUpdateDTORequest.setName("Valid Name");
        productUpdateDTORequest.setDescription("   ");

        savedProduct.setName("Latte");
        savedProduct.setDescription("Old Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(savedProduct));

        NotBlankException exception = assertThrows(NotBlankException.class,
                () -> productService.updateProduct(productId, productUpdateDTORequest));

        assertEquals("Missing statement", exception.getMessage());

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);
    }

    @Test
    void uploadProductImage_success_withOldImage_returnsProductDTOResponse() {
        Long productId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        Product product = new Product();
        product.setId(productId);
        product.setImageUrl("old-image-url.jpg");

        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setImageUrl("new-image-url.jpg");

        ProductDTOResponse expectedResponse = new ProductDTOResponse();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaStorage.storeProductImage(mockFile)).thenReturn("new-image-url.jpg");
        when(productRepository.saveAndFlush(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(expectedResponse);

        org.springframework.transaction.support.TransactionSynchronizationManager.initSynchronization();

        try {
            ProductDTOResponse actualResponse = productService.uploadProductImage(productId, mockFile);

            assertNotNull(actualResponse);
            assertEquals(expectedResponse, actualResponse);
            assertEquals("new-image-url.jpg", product.getImageUrl());

            verify(mediaStorage, never()).deleteProductImage("new-image-url.jpg");
        } finally {

            org.springframework.transaction.support.TransactionSynchronizationManager.clear();
        }
    }



    @Test
    void uploadProductImage_success_noOldImage_doesNotDeleteAnything() {
        Long productId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        Product product = new Product();
        product.setId(productId);
        product.setImageUrl(null);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaStorage.storeProductImage(mockFile)).thenReturn("new-image-url.jpg");
        when(productRepository.saveAndFlush(product)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            productService.uploadProductImage(productId, mockFile);
        });

        verify(mediaStorage, times(1)).deleteProductImage("new-image-url.jpg");

        verify(mediaStorage, never()).deleteProductImage(null);
    }


    @Test
    void uploadProductImage_productNotFound_throwsResourceNotFoundException() {
        Long productId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                productService.uploadProductImage(productId, mockFile)
        );

        assertEquals("Product was not found", exception.getMessage());
        verifyNoInteractions(mediaStorage);
        verify(productRepository, never()).save(any());
    }

    @Test
    void uploadProductImage_repositorySaveFails_deletesNewImageAndReThrows() {
        Long productId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        Product product = new Product();
        product.setId(productId);
        product.setImageUrl("old-image-url.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaStorage.storeProductImage(mockFile)).thenReturn("new-image-url.jpg");
        when(productRepository.saveAndFlush(product)).thenThrow(new RuntimeException("Database error"));


        assertThrows(RuntimeException.class, () ->
                productService.uploadProductImage(productId, mockFile)
        );

        verify(mediaStorage, times(1)).deleteProductImage("new-image-url.jpg");
        verify(mediaStorage, never()).deleteProductImage("old-image-url.jpg");
    }

    // TESTS FOR deleteProductImage
    // ==========================================

    @Test
    void deleteProductImage_success_withExistingImage() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setImageUrl("image-to-delete.jpg");

        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setImageUrl(null);

        ProductDTOResponse expectedResponse = new ProductDTOResponse();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(expectedResponse);

        ProductDTOResponse actualResponse = productService.deleteProductImage(productId);

        assertNotNull(actualResponse);
        assertNull(product.getImageUrl());
        verify(mediaStorage, times(1)).deleteProductImage("image-to-delete.jpg");
    }

    @Test
    void deleteProductImage_success_noImage_doesNotDeleteFile() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setImageUrl(null);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.deleteProductImage(productId);

        assertNull(product.getImageUrl());
        verify(mediaStorage, never()).deleteProductImage(anyString());
    }

    @Test
    void deleteProductImage_productNotFound_throwsResourceNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                productService.deleteProductImage(productId)
        );

        verifyNoInteractions(mediaStorage);
        verify(productRepository, never()).save(any());
    }
}

