package com.example.demo.service;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.dto.request.ProductFilterCriteria;
import com.example.demo.dto.request.ProductUpdateDTORequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductDTOResponse;
import com.example.demo.dto.response.ProductFilterDTOResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface ProductService {

    ProductDTOResponse save(ProductDTORequest productDTORequest);

    List<ProductDTOResponse> getAll();

    ProductDTOResponse getById(Long productDTOId);

    void hardDelete(Long productDTOId);

    void softDelete(Long productDTOId);

    PageResponse<ProductFilterDTOResponse> getProductFilter(
            ProductFilterCriteria criteria, Pageable pageable);

    ProductDTOResponse updateProduct(Long productId, ProductUpdateDTORequest updateDTORequest);

    ProductDTOResponse uploadProductImage(Long productId, MultipartFile file);

    ProductDTOResponse deleteProductImage(Long productId);

}
