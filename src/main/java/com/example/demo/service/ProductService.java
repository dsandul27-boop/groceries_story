package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;

import java.util.List;

public interface ProductService {

    ProductDTOResponse save(ProductDTORequest productDTORequest, Vendor vendor, ProductType productType);
    List<ProductDTOResponse> getAll ();
    ProductDTOResponse getById(Long productDTOId);
    void delete (Long productDTOId);

}
