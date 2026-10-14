package com.example.demo.controller;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;
import com.example.demo.service.CartItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-item")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-all-cart-items")
    public ResponseEntity<List<CartItemDTOResponse>> getAll(){
        return new ResponseEntity<>(cartItemService.getAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-cart-item-by-id/{id}")
    public ResponseEntity<CartItemDTOResponse> getById(@PathVariable @Positive(message = "ID must be positive") Long id){
        CartItemDTOResponse cartItemDTOResponse = cartItemService.getById(id);
        return new ResponseEntity<>(cartItemDTOResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/save-cart-item")
    public ResponseEntity<CartItemDTOResponse> save(@Valid @RequestBody CartItemDTORequest cartItemDTORequest, Authentication authentication){
        return new ResponseEntity<>(cartItemService.save(cartItemDTORequest, authentication.getName()), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update-cart-item/{cartItemId}")
    public ResponseEntity<CartItemDTOResponse> updateCartItem(@PathVariable @Valid Long cartItemId, Authentication authentication, @Valid @RequestBody CartItemDTORequest request){
        return new ResponseEntity<>(cartItemService.updateQuantity(cartItemId,authentication.getName(), request) ,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/delete-cart-item/{cartItemId}")
    public ResponseEntity delete(@PathVariable @Positive(message = "ID must be positive") Long cartItemId){
        cartItemService.delete(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
