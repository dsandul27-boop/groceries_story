package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public class BatchDTORequest {

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "quantity can't be negative")
    private Long quantity;
    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price can't be negative or 0")
    private BigDecimal purchasePrice;
    @NotNull(message = "Expiration date is required")
    private Instant expirationDate;
    @NotNull(message = "Product id can't bu null")
    private Long productId;
}
