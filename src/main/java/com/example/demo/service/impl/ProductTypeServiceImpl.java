package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.CategoryDTOResponse;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;
import com.example.demo.mapper.ProductTypeMapper;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.service.ProductTypeService;

import java.util.List;

public class ProductTypeServiceImpl implements ProductTypeService {

    private final ProductTypeRepository productTypeRepository;
    private final CategoryServiceImpl categoryService;
    private final ProductTypeMapper productTypeMapper;
    public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository, CategoryServiceImpl categoryService, ProductTypeMapper productTypeMapper) {
        this.productTypeRepository = productTypeRepository;
        this.categoryService = categoryService;
        this.productTypeMapper = productTypeMapper;
    }


    @Override
    public Long save(ProductTypeDTORequest productTypeDTORequest, Category category) {

        ProductType productType = productTypeRepository.save(productTypeMapper.toEntity(productTypeDTORequest, category));
        return  productTypeMapper.toDTO(productType).getId();


    }

    @Override
    public List<ProductTypeDTOResponse> getAll() {
        return List.of();
    }

    @Override
    public ProductTypeDTOResponse getById(Long productTypeDTOId) {
        return null;
    }

    @Override
    public void delete(Long productTypeDTOId) {

    }
}
