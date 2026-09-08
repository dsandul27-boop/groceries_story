package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductTypeDTORequest {

    private Long id;
    private String name;
    private String description;
    private Long categoryId;
}
