package com.example.demo.service;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.ProductType;

import java.util.List;

public interface ProductTypeService {

    ProductTypeDTOResponse save(ProductTypeDTORequest productTypeDTORequest);

    List<ProductTypeDTOResponse> getAll();

    ProductTypeDTOResponse getById(Long productTypeDTOId);

    void hardDelete(Long productTypeDTOId);

    void softDelete(Long productTypeDTOId);

    boolean existsByCategoryId(Long categoryId);

    List<ProductTypeDTOResponse> activeProductTypeList(List<ProductType> productTypeList);
}
