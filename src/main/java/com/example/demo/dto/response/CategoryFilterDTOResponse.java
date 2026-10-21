package com.example.demo.dto.response;


import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CategoryFilterDTOResponse {

    private Long id;
    private String categoryName;
    private String description;
    private ActiveStatus status;
    private List<ProductTypeFilterDTOResponse> productTypes;
}