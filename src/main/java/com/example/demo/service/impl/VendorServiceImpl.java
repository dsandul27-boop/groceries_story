package com.example.demo.service.impl;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.IsInactiveException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.VendorMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository ;
    private final VendorMapper vendorMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Autowired
    public VendorServiceImpl(VendorRepository vendorRepository, VendorMapper vendorMapper, ProductServiceImpl productService, ProductMapper productMapper, ProductRepository productRepository) {
        this.vendorRepository = vendorRepository;
        this.vendorMapper = vendorMapper;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public VendorDTOResponse save(VendorDTORequest vendorDTORequest) {
        Vendor vendor = vendorMapper.toEntity(vendorDTORequest);
        vendor.setActiveStatus(ActiveStatus.ACTIVE);
        return vendorMapper.toDTO(vendorRepository.save(vendor));
    }

    @Override
    public List<VendorDTOResponse> getAll() {
        List<Vendor> vendorList = vendorRepository.findAll();
        if (vendorList.isEmpty()){
            throw new ResourceNotFoundException("Vendor not found");
        }
        return activeDTOList(vendorList);
    }

    @Override
    public VendorDTOResponse getById(Long vendorId) {
        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);
        if (optionalVendor.isEmpty()){
            throw new ResourceNotFoundException("Vendor was not found");
        }

        if (optionalVendor.get().getActiveStatus().equals(ActiveStatus.INACTIVE)){
            throw new IsInactiveException("Not available");
        }
        return vendorMapper.toDTO(optionalVendor.get());
    }



    @Override
    public void hardDelete (Long vendorId){
        if (!vendorRepository.existsById(vendorId)) {
            throw new ResourceNotFoundException("Vendor not found");
        }
        boolean existProduct = productRepository.existsByVendorId(vendorId);
        if (existProduct){
            throw new EntityInUseException("Product dependencies exist");
        }
        vendorRepository.deleteById(vendorId);
    }

    @Override
    public void softDelete(Long vendorId) {
        Optional<Vendor> optionalVendor= vendorRepository.findById(vendorId);
        if (optionalVendor.isEmpty()) {
            throw new ResourceNotFoundException("Doesn't exist");
        }
        Vendor vendor = optionalVendor.get();
        if (vendor.getActiveStatus().equals(ActiveStatus.INACTIVE)) {
            throw new IsInactiveException("Vendor is already inactive");
        }
        vendor.setActiveStatus(ActiveStatus.INACTIVE);
        vendorRepository.save(vendor);
    }

    @Override
    public List<VendorDTOResponse> activeDTOList(List<Vendor> vendorList) {
        List<VendorDTOResponse> activeDTOResponse = new ArrayList<>();
        for (Vendor vendor: vendorList) {
            if (vendor.getActiveStatus().equals(ActiveStatus.ACTIVE)) {
                activeDTOResponse.add(vendorMapper.toDTO(vendor));
            }
        }
        return activeDTOResponse;
    }
}

