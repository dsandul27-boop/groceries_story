package com.example.demo.mapper;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.dto.response.ProductTypeFilterDTOResponse;
import com.example.demo.entities.ProductType;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeMapper {


    public ProductTypeDTOResponse toDTO(ProductType productType) {
        ProductTypeDTOResponse productTypeDTOResponse = new ProductTypeDTOResponse();
        productTypeDTOResponse.setId(productType.getId());
        productTypeDTOResponse.setName(productType.getName());
        productTypeDTOResponse.setCategoryId(productType.getCategory().getId());
        productTypeDTOResponse.setDescription(productType.getDescription());
        productTypeDTOResponse.setStatus(productType.getStatus());
        return productTypeDTOResponse;
    }

    ;

    public ProductType toEntity(ProductTypeDTORequest productTypeDTORequest) {
        ProductType productType = new ProductType();
        productType.setName(productTypeDTORequest.getName());
        productType.setDescription(productTypeDTORequest.getDescription());
        return productType;
    }

    public ProductType toEntity(ProductTypeDTOResponse productTypeDTOResponse) {
        ProductType productType = new ProductType();
        productType.setName(productTypeDTOResponse.getName());
        productType.setDescription(productTypeDTOResponse.getDescription());
        return productType;
    }

    public ProductTypeFilterDTOResponse toFilterDTO(ProductType productType) {
        ProductTypeFilterDTOResponse response = new ProductTypeFilterDTOResponse();

        response.setId(productType.getId());
        response.setName(productType.getName());
        response.setDescription(productType.getDescription());
        response.setStatus(productType.getStatus());
        return response;
    }
}
