package com.example.demo.service;

import com.example.demo.dto.request.CartDTORequest;
import com.example.demo.dto.response.CartDTOResponse;

import java.util.List;

public interface CartService {

    CartDTOResponse getById(Long cartId);
    List<CartDTOResponse> getAll();
    void delete(Long cartId, String email);
}
