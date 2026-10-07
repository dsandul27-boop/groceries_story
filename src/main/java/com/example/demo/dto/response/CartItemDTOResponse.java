package com.example.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItemDTOResponse {

    private Long id;
    private Long productId;
    private Long quantity;
    private Long cartId;
}
