package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductUpdateDTORequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
