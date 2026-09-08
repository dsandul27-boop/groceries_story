package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final VendorServiceImpl vendorService;
    private final ProductTypeServiceImpl productTypeService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, VendorServiceImpl vendorService, ProductTypeServiceImpl productTypeService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.vendorService = vendorService;
        this.productTypeService = productTypeService;
    }

    @Override
    public ProductDTOResponse save(ProductDTORequest productDTORequest, Vendor vendor, ProductType productType) {
        Product product = productRepository.save(productMapper.toEntity(productDTORequest, vendor,productType));
        return productMapper.toDTO(product);
    }

    @Override
    public List<ProductDTOResponse> getAll() {
        List<ProductDTOResponse> productDTOResponseList = new ArrayList<>();
        List<Product> productList = productRepository.findAll();
        for (int i = 0; i < productList.size(); i++) {
            productDTOResponseList.add(productMapper.toDTO(productList.get(i)));
        }
        return productDTOResponseList;
    }

    @Override
    public ProductDTOResponse getById(Long productDTOId) {
      return productMapper.toDTO(productRepository.findById(productDTOId).orElseThrow(() -> new RuntimeException("Product not found")));
    }

    @Override
    public void delete(Long productDTOId) {
        productRepository.deleteById(productDTOId);
    }
}