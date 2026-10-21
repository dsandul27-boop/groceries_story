package com.example.demo.dto.response;

import com.example.demo.entities.ActiveStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductTypeFilterDTOResponse {

    private Long id;
    private String name;
    private String description;
    private ActiveStatus status;
}