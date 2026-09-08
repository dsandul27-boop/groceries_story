package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;

import java.util.List;

public interface ProductService {

    ProductDTOResponse save(ProductDTORequest productDTORequest);
    List<ProductDTOResponse> getAll ();
    ProductDTOResponse getById(Long productDTOId);
    void delete (Long productDTOId);

}
