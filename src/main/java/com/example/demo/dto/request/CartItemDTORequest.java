package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTORequest {
    private Long quantity;
    private Long cartId;
    private Long productId;
}
