package com.example.demo.service.impl;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;
import com.example.demo.entities.Cart;
import com.example.demo.entities.CartItem;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartItemMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    private CartItemMapper cartItemMapper;
    private CartItemRepository cartItemRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private UserRepository userRepository;

    @Autowired
    public CartItemServiceImpl(CartItemMapper cartItemMapper, CartItemRepository cartItemRepository, ProductRepository productRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.cartItemMapper = cartItemMapper;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }


    @Override
    public CartItemDTOResponse save(CartItemDTORequest cartItemDTORequest, String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        Cart cart = cartRepository.findCartByUserEmail(email);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(optionalUser.get());
            cart.setCreatedAt(Instant.now());
            cart = cartRepository.save(cart);
        }

        Optional<Product> optionalProduct =
                productRepository.findById(cartItemDTORequest.getProductId());

        if (optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException("Product not found");
        }

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemDTORequest.getQuantity());
        cartItem.setProduct(optionalProduct.get());
        cartItem.setCart(cart);

        return cartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }


    @Override
    public CartItemDTOResponse getById(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        return cartItemMapper.toDTO(optionalCartItem.get());
    }

    @Override
    public List<CartItemDTOResponse> getAll(String authUser) {
        User caller = userRepository.findByEmail(authUser).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findCartByUserEmail(caller.getEmail());

        if (cart == null) {
            throw new ResourceNotFoundException("Cart was not found");
        }

        if (!cart.getUser().getId().equals(caller.getId())) {
            throw new AccessDeniedException("You cannot view another user's cart items");
        }
        List<CartItem> cartItemList = cartItemRepository.findAllByCartId(cart.getId());
        List<CartItemDTOResponse> cartItemDTOResponseList = new ArrayList<>();
        if (cartItemList.isEmpty()) {
            throw new ResourceNotFoundException("Cart items were not found");
        }
        for (CartItem cartItem : cartItemList) {
            cartItemDTOResponseList.add(cartItemMapper.toDTO(cartItem));
        }
        return cartItemDTOResponseList;
    }

    @Override
    public void delete(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            throw new ResourceNotFoundException("Cart item already doesn't exist");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public CartItemDTOResponse updateQuantity(Long cartItemId, String email, CartItemDTORequest cartItemDTORequest) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            throw new ResourceNotFoundException("Cart item was not found");
        }

        Cart cart = optionalCartItem.get().getCart();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!cart.getUser().getId().equals(optionalUser.get().getId())) {
            throw new AccessDeniedException(optionalUser.get().getFirstName() + " " + optionalUser.get().getLastName() + ", you cannot update another user's cart");
        }

        CartItem cartItem = optionalCartItem.get();
        cartItem.setQuantity(cartItemDTORequest.getQuantity());


        return cartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }


}
