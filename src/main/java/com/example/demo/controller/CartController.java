package com.example.demo.controller;

import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.service.impl.CartServiceImpl;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String callerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CartDTOResponse> cartList = cartService.getAll(callerEmail);
        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-cart-by-id/{cartId}")
    public ResponseEntity<CartDTOResponse> getById(
            @PathVariable @Positive(message = "Can't be null or negative") Long cartId) {
        String callerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(cartService.getById(cartId, callerEmail), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-cart-by-id/{cartId}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long cartId){
        cartService.delete(cartId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
