package com.example.demo.mapper;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTORequest productDTO(Product product) {
        return null;
    }

    public Product toEntity(ProductDTORequest productDTORequest) {
        Product product = new Product();
        product.setPrice(productDTORequest.getPrice());
        return product;
    }

    public ProductDTOResponse toDTO (Product product){
        ProductDTOResponse productDTOResponse =new ProductDTOResponse();
        productDTOResponse.setId(product.getId());
        productDTOResponse.setVendorId(product.getVendor().getId());
        productDTOResponse.setProductTypeId(product.getProductType().getId());
        productDTOResponse.setPrice(product.getPrice());
        productDTOResponse.setActiveStatus(product.getActiveStatus());
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