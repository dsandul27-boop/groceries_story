package com.example.demo.mapper;

import com.example.demo.dto.request.CartDTORequest;
import com.example.demo.dto.request.CategoryDTORequest;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.Cart;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CartMapper {

    public CartDTOResponse toDTO(Cart cart){
       CartDTOResponse cartDTOResponse = new CartDTOResponse();
       cartDTOResponse.setId(cart.getId());
       cartDTOResponse.setUserId(cart.getUser().getId());
       cartDTOResponse.setCreatedAt(cart.getCreatedAt());
       cartDTOResponse.setUpdateAt(cart.getUpdateAt());
       return cartDTOResponse;
    }

    public Cart toEntity(CartDTORequest cartDTORequest){
        Cart cart = new Cart();
        return cart;
    }

    public CartDTOResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTOResponse response = new CartDTOResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdateAt(cart.getUpdateAt());

        return response;
    }
}
