package com.example.demo.controller;

import com.example.demo.dto.request.ProductDTORequest;
import com.example.demo.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping("/save-productDTO")
    public ProductDTORequest save (@RequestBody ProductDTORequest productDTO){
        return  null;
    }
}
