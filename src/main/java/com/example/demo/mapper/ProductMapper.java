package com.example.demo.mapper;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTORequest productDTO(Product product) {
        return null;
    }

    public Product toEntity(ProductDTORequest productDTO) {
        Product product = new Product();
        return null;
    }
}
