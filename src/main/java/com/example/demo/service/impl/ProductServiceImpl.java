package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl {

    private static ProductRepository productRepository;
    private static ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper){
        this.productRepository=productRepository;
        this.productMapper=productMapper;
    }

    public ProductDTORequest save(ProductDTORequest productDTO){

    }

}
