package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductTypeMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

    private final ProductTypeRepository productTypeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTypeMapper productTypeMapper;
    private final ProductRepository productRepository;

    @Autowired
    public ProductTypeServiceImpl(
            ProductTypeRepository productTypeRepository,
            CategoryRepository categoryRepository,
            ProductTypeMapper productTypeMapper,
            ProductRepository productRepository) {

        this.productTypeRepository = productTypeRepository;
        this.categoryRepository = categoryRepository;
        this.productTypeMapper = productTypeMapper;
        this.productRepository = productRepository;
    }


    @Override
    public ProductTypeDTOResponse save(ProductTypeDTORequest productTypeDTORequest) {
        Category category = categoryRepository.findById(productTypeDTORequest.getCategoryId()).orElseThrow(() ->
        new ResourceNotFoundException("Category not found by Id"));
        ProductType productType = productTypeMapper.toEntity(productTypeDTORequest);
        productType.setCategory(category);
        productType.setActiveStatus(ActiveStatus.ACTIVE);
        ProductType productTypeResponse = productTypeRepository.save(productType);
        return productTypeMapper.toDTO(productTypeResponse);
    }

    @Override
    public List<ProductTypeDTOResponse> getAll() {
        List<ProductType> productTypeList = productTypeRepository.findAll();
        if (productTypeList.isEmpty()) {
            throw new ResourceNotFoundException("No product type found");
        }
        List<ProductTypeDTOResponse> productTypeDTOResponseList = new ArrayList<>();
        for (ProductType productType : productTypeList) {
            productTypeDTOResponseList.add(productTypeMapper.toDTO(productType));
        }
        return productTypeDTOResponseList;
    }

    @Override
    public ProductTypeDTOResponse getById(Long productTypeId) {
        Optional<ProductType> optionalProduct = productTypeRepository.findById(productTypeId);
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return productTypeMapper.toDTO(optionalProduct.get());
    }

    @Override
    public void delete(Long productTypeDTOId) {
        if (!productTypeRepository.existsById(productTypeDTOId)) {
            throw new ResourceNotFoundException("Already doesn't exist");
        }
        boolean productExist = productRepository.existsByProductTypeId(productTypeDTOId);
        if (productExist) {
            throw new EntityInUseException("Product dependencies exist");
        }
        productTypeRepository.deleteById(productTypeDTOId);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return productTypeRepository.existsByCategoryId(categoryId);
    }
}
