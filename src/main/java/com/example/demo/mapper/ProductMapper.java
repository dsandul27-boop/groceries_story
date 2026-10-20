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
        product.setPrice(productDTORequest.getPrice());
        return product;
    }

    public ProductDTOResponse toDTO(Product product) {
        ProductDTOResponse productDTOResponse = new ProductDTOResponse();
        productDTOResponse.setId(product.getId());
        productDTOResponse.setVendorId(product.getVendor().getId());
        productDTOResponse.setProductTypeId(product.getProductType().getId());
        productDTOResponse.setPrice(product.getPrice());
        productDTOResponse.setActiveStatus(product.getActiveStatus());
        productDTOResponse.setCreatedAt(product.getCreatedAt());
        return productDTOResponse;
    }

    public ProductFilterDTOResponse toFilterDTO(Product product) {
        ProductFilterDTOResponse productFilter = new ProductFilterDTOResponse();

        productFilter.setId(product.getId());
        productFilter.setPrice(product.getPrice());
        productFilter.setActiveStatus(product.getActiveStatus());
        productFilter.setCreatedAt(product.getCreatedAt());

        ProductTypeFilterDTOResponse productTypeFilterDTOResponse = productTypeMapper.toFilterDTO(product.getProductType());
        VendorFilterDTOResponse vendorFilterDTOResponse = vendorMapper.toFilerDTO((product.getVendor()));

        productFilter.setProductTypeFilterDTOResponse(productTypeFilterDTOResponse);
        productFilter.setVendorFilterDTOResponse(vendorFilterDTOResponse);

        return productFilter;
    }
}
