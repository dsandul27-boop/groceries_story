package com.example.demo.mapper;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductType;
import com.example.demo.entities.Vendor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class ProductMapper {

    public ProductDTORequest productDTO(Product product) {
        return null;
    }

    public Product toEntity(ProductDTORequest productDTORequest, Vendor vendor, ProductType productType) {
        Product product = new Product();
        product.setVendor(vendor);
        product.setProductType(productType);
        product.setPrice(productDTORequest.getPrice());
        product.setActive(productDTORequest.getActive());
        product.setCreatedAt(productDTORequest.getCreatedAt());
        return product;
    }

    public ProductDTOResponse toDTO (Product product){
        ProductDTOResponse productDTOResponse =new ProductDTOResponse();
        productDTOResponse.setVendorId(product.getId());
        productDTOResponse.setVendorId(product.getVendor().getId());
        productDTOResponse.setProductTypeId(product.getProductType().getId());
        productDTOResponse.setPrice(product.getPrice());
        product.setActive(product.isActive());
        productDTOResponse.setCreatedAt(product.getCreatedAt());
        return productDTOResponse;
    }
}
//private Long id;
//private Long vendorId;
//private Long productTypeId;
//private BigDecimal price;
//private Boolean active;
//private LocalDateTime createdAt;