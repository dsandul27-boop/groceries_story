package com.example.demo.service.impl;

import com.example.demo.dto.request.VendorDTORequest;
import com.example.demo.dto.response.VendorDTOResponse;
import com.example.demo.entities.Vendor;
import com.example.demo.mapper.VendorMapper;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository ;
    private final VendorMapper vendorMapper;

    @Autowired
    public VendorServiceImpl(VendorRepository vendorRepository, VendorMapper vendorMapper) {
        this.vendorRepository = vendorRepository;
        this.vendorMapper = vendorMapper;
    }

    public List<VendorDTOResponse> getAll() {
        List<Vendor> vendorList = vendorRepository.findAll();
        List<VendorDTOResponse> vendorDTOResponseList = new ArrayList<>();
        for (int i = 0; i <vendorList.size() ; i++) {
           vendorDTOResponseList.add(vendorMapper.toDTO(vendorList.get(i)));
        }
        return vendorDTOResponseList;
    }

    public VendorDTOResponse getById(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor not found"));
        return vendorMapper.toDTO(vendor);
    }

    public Long save(VendorDTORequest vendorDTORequest) {
        return vendorRepository.save(vendorMapper.toEntity(vendorDTORequest)).getId();
    }

    public void delete (Long vendorId){
        if (!vendorRepository.existsById(vendorId)) {
            throw new RuntimeException("Vendor not found");
        }
        vendorRepository.deleteById(vendorId);
    }
}

//findAll()
//findById()
//save()
//deleteById()
//existsById()
