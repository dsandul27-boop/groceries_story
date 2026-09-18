package com.example.demo.dto.request;

import com.example.demo.entities.ActiveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
