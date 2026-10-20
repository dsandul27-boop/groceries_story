package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.entities.Product;

import java.util.List;

public interface ProductService {

    ProductDTOResponse save(ProductDTORequest productDTORequest);

    List<ProductDTOResponse> getAll();

    ProductDTOResponse getById(Long productDTOId);

    void hardDelete(Long productDTOId);

    void softDelete(Long productDTOId);

    List<ProductFilterDTOResponse> getProductFilter();

    List<ProductDTOResponse> activeProductList(List<Product> productList);

    List<ProductFilterDTOResponse> activeProductFilterList(List<Product> productList);
}
