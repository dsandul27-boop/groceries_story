package com.example.demo.service;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;
import com.example.demo.entities.CartItem;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemService {

    CartItemDTOResponse save(CartItemDTORequest cartItemDTORequest);
    CartItemDTOResponse getById(Long cartItemId);
    List<CartItemDTOResponse> getAll();
    void delete(Long cartItemId);
}
