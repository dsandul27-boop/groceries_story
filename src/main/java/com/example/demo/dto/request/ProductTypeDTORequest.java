package com.example.demo.dto.request;

import com.example.demo.entities.ActiveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductTypeDTORequest {

    @NotBlank(message = "Missing field name")
    private String name;
    private String description;
    @NotNull(message = "Missing field categoryId")
    @Positive
    private Long categoryId;
}
