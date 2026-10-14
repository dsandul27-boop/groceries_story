package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.dto.request.CartDTORequest;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.User;
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
    private final Authentication authentication;

    @Autowired
    public CartController(CartServiceImpl cartService, Authentication authentication) {
        this.cartService = cartService;

        this.authentication = authentication;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/save-cart")
    public ResponseEntity<CartDTOResponse> save(@Valid @RequestBody CartDTORequest cartDTORequest){
       return new ResponseEntity<>(cartService.save(cartDTORequest),HttpStatus.OK);
    }

    @GetMapping("/get-all-carts")
    public ResponseEntity<List<CartDTOResponse>> getAll(){
        List<CartDTOResponse> cartList = cartService.getAll();
        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }

    @GetMapping("/get-cart-by-id/{cartId}")
    public ResponseEntity<CartDTOResponse> getById(@PathVariable @Positive(message = "Can't be null or negative") Long cartId){
        return new ResponseEntity<CartDTOResponse>(cartService.getById(cartId), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete-cart-by-id/{cartId}")
    public ResponseEntity delete(@PathVariable @Positive(message = "Cant be null or negative") Long cartId){
        String email = authentication.getName();
        cartService.delete(cartId, email);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
