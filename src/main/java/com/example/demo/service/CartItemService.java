package com.example.demo.service;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;
import com.example.demo.entities.CartItem;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CartItemService {

    CartItemDTOResponse save(CartItemDTORequest cartItemDTORequest, String name);
    CartItemDTOResponse getById(Long cartItemId);
    List<CartItemDTOResponse> getAll(String authUser);
    void delete(Long cartItemId);
    CartItemDTOResponse updateQuantity(Long cartItemId, String email, CartItemDTORequest cartItemDTORequest);
}
