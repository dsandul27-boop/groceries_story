package com.example.demo.service;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
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
    void getById_shouldReturnVendorDTO_whenVendorExists() {

        VendorDTOResponse savedVendorDTOResponse = new VendorDTOResponse();

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
        verify(vendorMapper.toEntity(vendorDTORequest));
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
    void save_shouldReturnSavedVendor() {

        when(vendorMapper.toEntity(vendorDTORequest))
                .thenReturn(vendor);
        when(vendorRepository.save(vendor))
                .thenReturn(savedVendor);
        when(vendorMapper.toDTO(savedVendor)).thenReturn(vendorDTOResponse);


        // Act
        VendorDTOResponse result = vendorService.save(vendorDTORequest);

        // Assert
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
    void getAll_shouldReturnAllVendors() {

        List<Vendor> vendorList = new ArrayList<>();
        vendorList.add(0, vendor);

        when(vendorRepository.findAll()).thenReturn(vendorList);
        when(vendorMapper.toDTO(vendor)).thenReturn(vendorDTOResponse);

        List<VendorDTOResponse> resultList = vendorService.getAll();

        assertNotNull(resultList);
        assertEquals(1L, resultList.get(0).getId());
        assertEquals("Latti", resultList.get(0).getName());
        assertEquals(ActiveStatus.ACTIVE, resultList.get(0).getStatus());
        verify(vendorRepository).findAll();
        verify(vendorMapper).toDTO(vendor);
    }

    @Test
    void getAll_shouldReturnResourceNotFoundExceptionIfVendorsListIsEmpty() {
        Vendor vendor = new Vendor();
        List<Vendor> vendorList = new ArrayList<>();

        when(vendorRepository.findAll()).thenReturn(vendorList);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> vendorService.getAll());

        assertEquals("Vendor not found", exception.getMessage());
        verify(vendorRepository).findAll();
        verifyNoInteractions(vendorMapper);
    }

    //_________________________________________________________________________________________________________________
    @Test
    void delete_shouldDeleteOneVendorById() {

        Long vendorId = 1L;
        when(vendorRepository.existsById(vendorId)).thenReturn(true);
        when(productRepository.existsByVendorId(vendorId)).thenReturn(false);

        vendorService.delete(vendorId);

        verify(vendorRepository).existsById(vendorId);
        verify(productRepository).existsByVendorId(vendorId);
        verify(vendorRepository).deleteById(vendorId);
    }

    @Test
    void delete_shouldThrowResourceNotFoundException_whenVendorDoesNotExist() {

        // Arrange
        Long vendorId = 1L;

        when(vendorRepository.existsById(vendorId)).thenReturn(false);

        // Act + Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vendorService.delete(vendorId)
        );

        // Assert
        assertEquals("Vendor not found", exception.getMessage());

        verify(vendorRepository).existsById(vendorId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void delete_shouldEntityInUseException_WhenProductExist() {
        Long vendorId = 1L;
        when(vendorRepository.existsById(vendorId)).thenReturn(true);
        when(productRepository.existsByVendorId(vendorId)).thenReturn(true);
        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> vendorService.delete(vendorId));

        assertEquals("Product dependencies exist", exception.getMessage());
        verify(vendorRepository).existsById(vendorId);
        verify(productRepository).existsByVendorId(vendorId);
        verify(vendorRepository, never()).deleteById(vendorId);
    }
}
