package com.example.demo.service.impl;

import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.Cart;
import com.example.demo.entities.User;
import com.example.demo.exception.EntityInUseException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
    public CartServiceImpl(CartMapper cartMapper, CartRepository cartRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }


    @Override
    public CartDTOResponse getById(Long cartId, String callerEmail) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart was not found"));
        User caller = userRepository.findByEmail(callerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!cart.getUser().getId().equals(caller.getId())) {
            throw new AccessDeniedException("You cannot view another user's cart");
        }
        return cartMapper.toDTO(cart);
    }

    @Override
    public List<CartDTOResponse> getAll(String authUser) {

        User caller = userRepository.findByEmail(authUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findCartByUserEmail(caller.getEmail());

        if (cart == null) {
            throw new ResourceNotFoundException("Cart was not found");
        }

        List<CartDTOResponse> cartDTOResponseList = new ArrayList<>();
        cartDTOResponseList.add(cartMapper.toDTO(cart));

        return cartDTOResponseList;
    }

    @Override
    public void delete(Long cartId) {

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isEmpty()) {
            throw new ResourceNotFoundException("Cart already doesn't exist");
        }

        boolean existCart = cartItemRepository.existsByCartId(cartId);
        if (existCart) {
            throw new EntityInUseException("Exist cartItem dependencies");
        }
        cartRepository.deleteById(cartId);
    }
}
