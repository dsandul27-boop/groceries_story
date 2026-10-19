package com.example.demo.service;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;

import java.util.List;

public interface CartItemService {

    CartItemDTOResponse save(CartItemDTORequest cartItemDTORequest, String name);
    CartItemDTOResponse getById(Long cartItemId, String authUser);
    List<CartItemDTOResponse> getAll(String authUser);
    void delete(Long cartItemId, String authUser);
    CartItemDTOResponse updateQuantity(Long cartItemId, String email, CartItemDTORequest cartItemDTORequest);
}
