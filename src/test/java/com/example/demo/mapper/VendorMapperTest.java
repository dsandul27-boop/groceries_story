package com.example.demo.mapper;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.Vendor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VendorMapperTest {

    private VendorMapper vendorMapper;

    @BeforeEach
    void setUp() {
        vendorMapper = new VendorMapper();
    }

    @Test
    void toDTO_shouldMapVendorToDTO() {

        // Arrange
        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setName("Latti");

        // Act
        VendorDTOResponse result = vendorMapper.toDTO(vendor);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Latti", result.getName());
    }

    @Test
    void toEntity_shouldMapVendorDTOToVendor(){
        VendorDTORequest vendorDTORequest = new VendorDTORequest();
        vendorDTORequest.setName("Latti");

        Vendor vendor = vendorMapper.toEntity(vendorDTORequest);

        assertEquals("Latti", vendor.getName());

    }
}
