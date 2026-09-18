package com.example.demo.mapper;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.ProductType;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeMapper {


    public ProductTypeDTOResponse toDTO(ProductType productType) {
        ProductTypeDTOResponse productTypeDTOResponse = new ProductTypeDTOResponse();
        productTypeDTOResponse.setId(productType.getId());
        productTypeDTOResponse.setName(productType.getName());
        productTypeDTOResponse.setCategoryId(productType.getCategory().getId());
        productTypeDTOResponse.setDescription(productType.getProductDescription());
        productTypeDTOResponse.setStatus(productType.getActiveStatus());
        return productTypeDTOResponse;
    }

    ;

    public ProductType toEntity(ProductTypeDTORequest productTypeDTORequest) {
        ProductType productType = new ProductType();
        productType.setName(productTypeDTORequest.getName());
        productType.setProductDescription(productTypeDTORequest.getDescription());
        return productType;
    }

    public ProductType toEntity(ProductTypeDTOResponse productTypeDTOResponse) {
        ProductType productType = new ProductType();
        productType.setName(productTypeDTOResponse.getName());
        productType.setProductDescription(productTypeDTOResponse.getDescription());
        return productType;
    }
}
