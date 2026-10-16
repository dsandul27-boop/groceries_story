package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductDTORequest {

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
