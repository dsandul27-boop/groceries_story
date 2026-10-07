package com.example.demo.mapper;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.dto.response.VendorFilterDTOResponse;
import com.example.demo.entities.Vendor;
import org.springframework.stereotype.Component;

@Component
public class VendorMapper {

    public VendorDTOResponse toDTO(Vendor vendor) {
        VendorDTOResponse vendorDTOResponse = new VendorDTOResponse();
        vendorDTOResponse.setId(vendor.getId());
        vendorDTOResponse.setName(vendor.getName());
        vendorDTOResponse.setStatus(vendor.getActiveStatus());
        return vendorDTOResponse;
    }

    public Vendor toEntity(VendorDTORequest vendorDTORequest) {
        Vendor vendor = new Vendor();
        vendor.setName(vendorDTORequest.getName());
        return vendor;
    }

    public Vendor toEntity(VendorDTOResponse vendorDTOResponse) {
        Vendor vendor = new Vendor();
        vendor.setName(vendorDTOResponse.getName());
        return vendor;
    }

    public VendorFilterDTOResponse toFilerDTO(Vendor vendor){
        VendorFilterDTOResponse vendorFilterDTOResponse = new VendorFilterDTOResponse();
        vendorFilterDTOResponse.setId(vendor.getId());
        vendorFilterDTOResponse.setName(vendor.getName());
        vendorFilterDTOResponse.setActiveStatus(vendor.getActiveStatus());
        return  vendorFilterDTOResponse;
    }
}
