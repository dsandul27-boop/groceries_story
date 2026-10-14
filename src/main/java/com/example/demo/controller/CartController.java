package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import com.example.demo.dto.request.CartDTORequest;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.service.impl.CartServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartServiceImpl cartService;

    @Autowired
    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @GetMapping("/get-all-carts")
    public ResponseEntity<List<CartDTOResponse>> getAll(){
        List<CartDTOResponse> cartList = cartService.getAll();
        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-cart-by-id/{cartId}")
    public ResponseEntity<CartDTOResponse> getById(@PathVariable @Positive(message = "Can't be null or negative") Long cartId){
        return new ResponseEntity<CartDTOResponse>(cartService.getById(cartId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-cart-by-id/{cartId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long cartId, Authentication authentication){
        cartService.delete(cartId, authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
