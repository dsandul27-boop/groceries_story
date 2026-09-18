package com.example.demo.service;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.Vendor;

import java.util.List;

public interface VendorService {

    List<VendorDTOResponse> getAll();

    VendorDTOResponse getById(Long vendorDTOId);

    VendorDTOResponse save(VendorDTORequest vendorDTORequest);

    void delete (Long vendorDTOId);
}
