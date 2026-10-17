package com.example.demo.service;

import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;


public interface CartService {

    CartDTOResponse getById(Long cartId);
    // În CartService.java
    PageResponse<CartDTOResponse> getAll(Pageable pageable);

    void delete(Long cartId);
    CartDTOResponse getByEmail(String email);
}
