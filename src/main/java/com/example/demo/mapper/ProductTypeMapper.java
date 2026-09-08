package com.example.demo.mapper;

import com.example.demo.dto.request.ProductTypeDTORequest;
import com.example.demo.dto.response.ProductTypeDTOResponse;
import com.example.demo.entities.Category;
import com.example.demo.entities.ProductType;
import com.example.demo.service.impl.CategoryServiceImpl;
import com.example.demo.service.impl.VendorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeMapper {


    public ProductTypeDTOResponse toDTO(ProductType productType){
        ProductTypeDTOResponse productTypeDTOResponse = new ProductTypeDTOResponse();
        productTypeDTOResponse.setId(productType.getId());
        productTypeDTOResponse.setName(productType.getName());
        productTypeDTOResponse.setCategoryId(productTypeDTOResponse.getCategoryId());
        productTypeDTOResponse.setDescription(productType.getProductDescription());
        return productTypeDTOResponse;
    };

    public ProductType toEntity(ProductTypeDTORequest productTypeDTORequest, Category category){
        ProductType productType = new ProductType();
        productType.setName(productTypeDTORequest.getName());
        productType.setCategory(category);
        productType.setProductDescription(productTypeDTORequest.getDescription());
      return  productType;
    }




}
