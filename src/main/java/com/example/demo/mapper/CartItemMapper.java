package com.example.demo.mapper;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;
import com.example.demo.entities.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {

    public CartItemDTOResponse toDTO(CartItem cartItem){
        CartItemDTOResponse cartItemDTOResponse = new CartItemDTOResponse();
        cartItemDTOResponse.setId(cartItem.getId());
        cartItemDTOResponse.setProductId(cartItem.getProduct().getId());
        cartItemDTOResponse.setCartId(cartItem.getCart().getId());
        cartItemDTOResponse.setQuantity(cartItem.getQuantity());
        return cartItemDTOResponse;
    }

    public CartItem toEntity(CartItemDTORequest cartItemDTORequest){
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemDTORequest.getQuantity());
        return cartItem;
    }
}
