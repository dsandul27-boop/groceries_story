package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductDTORequest {

    @Size(max = 100)
    @NotBlank(message = "Missing field name")
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Missing field vendorId")
    @Positive(message = "Id can't be negative")
    private Long vendorId;

    @NotNull(message = "Missing field productTypeId")
    @Positive(message = "Id can't be negative")
    private Long productTypeId;

    @NotNull(message = "Missing field price")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

}
