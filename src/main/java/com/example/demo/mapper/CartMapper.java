package com.example.demo.mapper;

import com.example.demo.dto.request.CartDTORequest;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartDTOResponse toDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartDTOResponse cartDTOResponse = new CartDTOResponse();
        cartDTOResponse.setId(cart.getId());
        cartDTOResponse.setUserId(cart.getUser().getId());
        cartDTOResponse.setCreatedAt(cart.getCreatedAt());
        cartDTOResponse.setUpdateAt(cart.getUpdateAt());
        return cartDTOResponse;
    }

    public Cart toEntity(CartDTORequest cartDTORequest) {
        Cart cart = new Cart();
        return cart;
    }
}
