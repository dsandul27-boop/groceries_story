package com.example.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTOResponse {

    private Long id;
    private String categoryName;
    private String description;
}
