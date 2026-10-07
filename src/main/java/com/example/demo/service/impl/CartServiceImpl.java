package com.example.demo.service.impl;

import com.example.demo.dto.request.CartDTORequest;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.Cart;
import com.example.demo.entities.CartItem;
import com.example.demo.entities.User;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private CartMapper cartMapper;
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CartItemRepository cartItemRepository;

    @Autowired
    public CartServiceImpl (CartMapper cartMapper, CartRepository cartRepository, UserRepository userRepository, CartItemRepository cartItemRepository){
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public CartDTOResponse save(CartDTORequest cartDTORequest) {
        Optional<User> user = userRepository.findById(cartDTORequest.getUserId());
        if (user.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        Cart cart = cartMapper.toEntity(cartDTORequest);
        cart.setUser(user.get());
        cart.setCreatedAt(Instant.now());
        return cartMapper.toDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTOResponse getById(Long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isEmpty()){
            throw new ResourceNotFoundException("Cart was not found");
        }
        return cartMapper.toDTO(optionalCart.get());
    }

    @Override
    public List<CartDTOResponse> getAll() {
        List<Cart> cartList = cartRepository.findAll();
        List<CartDTOResponse> cartDTOResponseList = new ArrayList<>();
        if (cartList.isEmpty()){
            throw new ResourceNotFoundException("No one cart was found");
        }
        for (Cart cart : cartList){
            cartDTOResponseList.add(cartMapper.toDTO(cart));
        }
        return List.of();
    }

    @Override
    public void delete(Long cartId) {
        if (!cartRepository.existsById(cartId)){
            throw new ResourceNotFoundException("Cart already doesn't exist");
        }
        boolean existCart = cartItemRepository.existsById(cartId);
        if (existCart){
            throw new EntityInUseException("Exist cartItem dependencies");
       }
    cartRepository.deleteById(cartId);
    }
}
