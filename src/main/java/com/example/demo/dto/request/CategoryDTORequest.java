package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTORequest {

    private Long id;
    private String categoryName;
    private String description;
}
