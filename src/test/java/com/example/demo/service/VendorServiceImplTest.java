package com.example.demo.service;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.IsInactiveException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.VendorMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.impl.VendorServiceImpl;
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
public class VendorServiceImplTest {

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private VendorMapper vendorMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    private Vendor vendor;
    private Vendor savedVendor;
    private VendorDTORequest vendorDTORequest;
    private VendorDTOResponse vendorDTOResponse;

    @BeforeEach
    void setUp() {

        vendorDTORequest = new VendorDTORequest();
        vendorDTORequest.setName("Latti");

        vendor = new Vendor();
        vendor.setId(1L);
        vendor.setName("Latti");
        vendor.setActiveStatus(ActiveStatus.ACTIVE);

        savedVendor = new Vendor();
        savedVendor.setId(1L);
        savedVendor.setName("Latti");
        savedVendor.setActiveStatus(ActiveStatus.ACTIVE);

        vendorDTOResponse = new VendorDTOResponse();
        vendorDTOResponse.setId(1L);
        vendorDTOResponse.setName("Latti");
        vendorDTOResponse.setStatus(ActiveStatus.ACTIVE);
    }

    @Test
    void save_shouldSaveVendorAndReturnVendorDTOResponse() {

        VendorDTOResponse savedVendorDTOResponse = new VendorDTOResponse();
        savedVendorDTOResponse.setId(1L);
        savedVendorDTOResponse.setName("Latti");
        savedVendorDTOResponse.setStatus(ActiveStatus.ACTIVE);

        when(vendorMapper.toEntity(vendorDTORequest))
                .thenReturn(vendor);
        vendor.setActiveStatus(ActiveStatus.ACTIVE);
        when(vendorRepository.save(vendor))
                .thenReturn(savedVendor);
        when(vendorMapper.toDTO(savedVendor)).thenReturn(savedVendorDTOResponse);

        // Act
        VendorDTOResponse result = vendorService.save(vendorDTORequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Latti", result.getName());
        verify(vendorRepository).save(vendor);
        verify(vendorMapper).toDTO(vendor);
        verify(vendorMapper).toEntity(vendorDTORequest);
    }

    @Test
    void save_shouldReturnSavedVendor() {

        when(vendorMapper.toEntity(vendorDTORequest))
                .thenReturn(vendor);
        when(vendorRepository.save(vendor))
                .thenReturn(savedVendor);
        when(vendorMapper.toDTO(savedVendor)).thenReturn(vendorDTOResponse);

        VendorDTOResponse result = vendorService.save(vendorDTORequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Latti", result.getName());
        assertEquals(ActiveStatus.ACTIVE, result.getStatus());
        verify(vendorMapper).toEntity(vendorDTORequest);
        verify(vendorRepository).save(vendor);
        verify(vendorMapper).toDTO(savedVendor);
    }

    //______________________________________________________________________________________________________________________

    @Test
    void getById_shouldReturnVendorById() {
        Long vendorId = 1L;
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(vendorMapper.toDTO(vendor)).thenReturn(vendorDTOResponse);

        VendorDTOResponse result = vendorService.getById(vendorId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Latti", result.getName());
        verify(vendorRepository).findById(vendorId);
        verify(vendorMapper).toDTO(vendor);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenVendorDoesNotExist() {

        // Arrange
        Long vendorId = 1L;

        when(vendorRepository.findById(vendorId))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vendorService.getById(vendorId)
        );

        assertEquals("Vendor was not found", exception.getMessage());

        verify(vendorRepository).findById(vendorId);
        verifyNoInteractions(vendorMapper);
    }

    //______________________________________________________________________________________________________________________
    @Test
    void getAll_shouldReturnAllVendors() {

        List<Vendor> vendorList = new ArrayList<>();
        vendorList.add(0, vendor);

        when(vendorRepository.findAll()).thenReturn(vendorList);
        when(vendorMapper.toDTO(vendor)).thenReturn(vendorDTOResponse);

        List<VendorDTOResponse> resultList = vendorService.getAll();

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.get(0).getId());
        assertEquals("Latti", resultList.get(0).getName());
        assertEquals(ActiveStatus.ACTIVE, resultList.get(0).getStatus());
        verify(vendorRepository).findAll();
        verify(vendorMapper).toDTO(vendor);
    }

    @Test
    void getAll_shouldReturnResourceNotFoundExceptionIfVendorsListIsEmpty() {
        List<Vendor> vendorList = new ArrayList<>();

        when(vendorRepository.findAll()).thenReturn(vendorList);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> vendorService.getAll());

        assertEquals("Vendor not found", exception.getMessage());
        verify(vendorRepository).findAll();
        verifyNoInteractions(vendorMapper);
    }

    @Test
    void getAll_shouldIncludeInactiveCategories_AC3() {
        Vendor activeVendor = new Vendor();
        activeVendor.setId(1L);
        activeVendor.setActiveStatus(ActiveStatus.ACTIVE);

        Vendor inactiveVendor = new Vendor();
        inactiveVendor.setId(2L);
        inactiveVendor.setActiveStatus(ActiveStatus.INACTIVE);

        when(vendorRepository.findAll()).thenReturn(List.of(activeVendor, inactiveVendor));
        when(vendorMapper.toDTO(activeVendor)).thenReturn(new VendorDTOResponse());
        when(vendorMapper.toDTO(inactiveVendor)).thenReturn(new VendorDTOResponse());
        List<VendorDTOResponse> result = vendorService.getAll();

        assertEquals(2, result.size());
        verify(vendorMapper).toDTO(inactiveVendor);
        verify(vendorMapper).toDTO(activeVendor);
    }

    //_________________________________________________________________________________________________________________
    // --- SOFT DELETE TESTS ---

    @Test
    void softDelete_shouldFlipStatusToInactive() {
        Long vendorId = 1L;
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        vendorService.softDelete(vendorId);

        assertEquals(ActiveStatus.INACTIVE, vendor.getActiveStatus());
        verify(vendorRepository).findById(vendorId);
        verify(vendorRepository).save(vendor);
    }

    @Test
    void softDelete_shouldThrowResourceNotFoundException_whenVendorDoesNotExist() {
        Long vendorId = 1L;
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vendorService.softDelete(vendorId)
        );

        assertEquals("Doesn't exist", exception.getMessage());
        verify(vendorRepository).findById(vendorId);
        verify(vendorRepository, never()).save(any());
    }

    @Test
    void softDelete_shouldThrowIsInactiveException_whenAlreadyInactive() {
        Long vendorId = 1L;
        vendor.setActiveStatus(ActiveStatus.INACTIVE);
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        IsInactiveException exception = assertThrows(
                IsInactiveException.class,
                () -> vendorService.softDelete(vendorId)
        );

        assertEquals("Vendor is already inactive", exception.getMessage());
        verify(vendorRepository).findById(vendorId);
        verify(vendorRepository, never()).save(any());
    }

    // --- HARD DELETE TESTS ---

    @Test
    void hardDelete_shouldDeleteOneVendorById() {
        Long vendorId = 1L;
        when(vendorRepository.existsById(vendorId)).thenReturn(true);
        when(productRepository.existsByVendorId(vendorId)).thenReturn(false);

        vendorService.hardDelete(vendorId);

        verify(vendorRepository).existsById(vendorId);
        verify(productRepository).existsByVendorId(vendorId);
        verify(vendorRepository).deleteById(vendorId);
    }

    @Test
    void hardDelete_shouldThrowResourceNotFoundException_whenVendorDoesNotExist() {
        Long vendorId = 1L;
        when(vendorRepository.existsById(vendorId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vendorService.hardDelete(vendorId)
        );

        assertEquals("Vendor not found", exception.getMessage());
        verify(vendorRepository).existsById(vendorId);
        verifyNoInteractions(productRepository);
        verify(vendorRepository, never()).deleteById(anyLong());
    }

    @Test
    void hardDelete_shouldThrowEntityInUseException_WhenProductExist() {
        Long vendorId = 1L;
        when(vendorRepository.existsById(vendorId)).thenReturn(true);
        when(productRepository.existsByVendorId(vendorId)).thenReturn(true);
        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> vendorService.hardDelete(vendorId));
        assertEquals("Product dependencies exist", exception.getMessage());
        verify(vendorRepository).existsById(vendorId);
        verify(productRepository).existsByVendorId(vendorId);
        verify(vendorRepository, never()).deleteById(vendorId);
    }
}
