package com.example.demo.mapper;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import com.example.demo.dto.response.ProductTypeFilterDTOResponse;
import com.example.demo.dto.response.VendorFilterDTOResponse;
import com.example.demo.entities.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ProductTypeMapper productTypeMapper;
    private final VendorMapper vendorMapper;
    private final ProductTypeRepository productTypeRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ProductMapper(ProductTypeMapper productTypeMapper, VendorMapper vendorMapper, ProductTypeRepository productTypeRepository, VendorRepository vendorRepository, ProductRepository productRepository) {
        this.productTypeMapper = productTypeMapper;
        this.vendorMapper = vendorMapper;
        this.productTypeRepository = productTypeRepository;
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
    }


    public Product toEntity(ProductDTORequest productDTORequest) {
        Product product = new Product();
        product.setName(productDTORequest.getName());
        product.setDescription(productDTORequest.getDescription());
        product.setPrice(productDTORequest.getPrice());
        return product;
    }

    public ProductDTOResponse toDTO(Product product) {
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setName(product.getName());
        productDTOResponse.setDescription(product.getDescription());
        productDTOResponse.setId(product.getId());
        productDTOResponse.setVendorId(product.getVendor().getId());
        productDTOResponse.setProductTypeId(product.getProductType().getId());
        productDTOResponse.setPrice(product.getPrice());
        productDTOResponse.setStatus(product.getActiveStatus());
        productDTOResponse.setCreatedAt(product.getCreatedAt());
        productDTOResponse.setImageUrl(product.getImageUrl());
        return productDTOResponse;
    }

    public ProductFilterDTOResponse toFilterDTO(Product product) {
        ProductFilterDTOResponse productFilter = new ProductFilterDTOResponse();

        productFilter.setId(product.getId());
        productFilter.setName(product.getName());
        productFilter.setDescription(product.getDescription());
        productFilter.setPrice(product.getPrice());
        productFilter.setStatus(product.getActiveStatus());
        productFilter.setCreatedAt(product.getCreatedAt());

        ProductTypeFilterDTOResponse productTypeFilterDTOResponse = productTypeMapper.toFilterDTO(product.getProductType());
        VendorFilterDTOResponse vendorFilterDTOResponse = vendorMapper.toFilerDTO((product.getVendor()));

        productFilter.setProductTypeFilterDTOResponse(productTypeFilterDTOResponse);
        productFilter.setVendorFilterDTOResponse(vendorFilterDTOResponse);

        productFilter.setImageUrl(product.getImageUrl());

        return productFilter;
    }
}
