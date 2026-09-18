package com.example.demo.service;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;

import java.util.List;

public interface ProductTypeService {

    ProductTypeDTOResponse save(ProductTypeDTORequest productTypeDTORequest);

    List<ProductTypeDTOResponse> getAll();

    ProductTypeDTOResponse getById(Long productTypeDTOId);

    void delete(Long productTypeDTOId);

    boolean existsByCategoryId(Long categoryId);

}
