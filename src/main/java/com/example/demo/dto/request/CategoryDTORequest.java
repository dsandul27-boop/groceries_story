package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDTORequest {

    @NotBlank(message = "Missing field categoryName")
    private String categoryName;
    private String description;
}
