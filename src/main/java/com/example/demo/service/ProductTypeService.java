package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;

import java.util.List;

public interface ProductTypeService {

    Long save(ProductTypeDTORequest productTypeDTORequest, Category category);

    List<ProductTypeDTOResponse> getAll();

    ProductTypeDTOResponse getById(Long productTypeDTOId);

    void delete(Long productTypeDTOId);

}
