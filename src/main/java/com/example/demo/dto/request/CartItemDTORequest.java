package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTORequest {

    @NotNull(message = "Can't be null or empty")
    @Positive(message = "Quantity must be positive or bigger than 0")
    private Long quantity;
    private Long cartId;
    private Long productId;
}
