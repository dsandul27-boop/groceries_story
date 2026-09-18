package com.example.demo.service.impl;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.mapper.ProductTypeMapper;
import com.example.demo.mapper.VendorMapper;
import com.example.demo.repository.BatchRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.repository.VendorRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final VendorRepository vendorRepository;
    private final BatchRepository batchRepository;
    private final ProductTypeRepository productTypeRepository;


    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, VendorRepository vendorRepository, BatchRepository batchRepository, ProductTypeRepository productTypeRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.vendorRepository = vendorRepository;
        this.batchRepository = batchRepository;
        this.productTypeRepository = productTypeRepository;
    }

    @Override
    public ProductDTOResponse save(ProductDTORequest productDTORequest) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(productDTORequest.getVendorId());
        if (vendorOptional.isEmpty()) {
            throw new ResourceNotFoundException("Vendor is empty");
        }
        Optional<ProductType> productTypeOptional = (productTypeRepository.findById(productDTORequest.getProductTypeId()));
        if (productTypeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product type is empty");
        }
        Product product = productMapper.toEntity(productDTORequest);
        product.setVendor(vendorOptional.get());
        product.setProductType(productTypeOptional.get());
        product.setActiveStatus(ActiveStatus.INACTIVE);
        product.setCreatedAt(Instant.now());
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public List<ProductDTOResponse> getAll() {
        List<ProductDTOResponse> productDTOResponseList = new ArrayList<>();
        List<Product> productList = productRepository.findAll();
        if (productList.isEmpty()) {
            throw new ResourceNotFoundException("No one product found");
        }
        for (Product product : productList) {
            productDTOResponseList.add(productMapper.toDTO(product));
        }
        return productDTOResponseList;
    }

    @Override
    public ProductDTOResponse getById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product was not found");
        }
        return productMapper.toDTO(optionalProduct.get());
    }

    @Override
    public void delete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product already doesn't exist");
        }
        boolean existBatch = batchRepository.existsByProductId(productId);
        if (existBatch){
            throw new EntityInUseException("Exist batch dependencies");
        }
        productRepository.deleteById(productId);
    }

}

