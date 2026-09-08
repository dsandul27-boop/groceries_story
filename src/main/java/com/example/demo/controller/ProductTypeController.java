package com.example.demo.controller;

import com.example.demo.service.ProductTypeService;
import com.example.demo.service.impl.ProductServiceImpl;
import com.example.demo.service.impl.ProductTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product_type")
public class ProductTypeController {


    private final ProductTypeServiceImpl productTypeService;

    @Autowired
    public ProductTypeController(ProductTypeServiceImpl productTypeService) {
        this.productTypeService = productTypeService;
    }

    public void jana(){

    }
}
