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
import org.springframework.stereotype.Service;

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
    public CartItemServiceImpl(CartItemMapper cartItemMapper, CartItemRepository cartItemRepository, ProductRepository productRepository, CartRepository cartRepository, UserRepository userRepository){
        this.cartItemMapper = cartItemMapper;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CartItemDTOResponse save(CartItemDTORequest cartItemDTORequest) {
        Optional<Cart> cart = cartRepository.findById(cartItemDTORequest.getCartId());
        if (cart.isEmpty()){
            throw new ResourceNotFoundException("Cart not found");
        }
        Optional<Product> product = productRepository.findById(cartItemDTORequest.getProductId());
        if (product.isEmpty()){
            throw new ResourceNotFoundException("Product not found");
        }
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemDTORequest.getQuantity());
        cartItem.setProduct(product.get());
        cartItem.setCart(cart.get());
        return cartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDTOResponse getById(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()){
            throw new ResourceNotFoundException("Cart item not found");
        }
        return cartItemMapper.toDTO(optionalCartItem.get());
    }

    @Override
    public List<CartItemDTOResponse> getAll() {
        List<CartItem> cartItemList = cartItemRepository.findAll();
        List<CartItemDTOResponse> cartItemDTOResponseList = new ArrayList<>();
        if (cartItemList.isEmpty()){
            throw new ResourceNotFoundException("Cart items were not found");
        }
        for (CartItem cartItem : cartItemList){
            cartItemDTOResponseList.add(cartItemMapper.toDTO(cartItem));
        }
        return cartItemDTOResponseList;
    }

    @Override
    public void delete(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()){
            throw new ResourceNotFoundException("Cart item already doesn't exist");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public CartItemDTOResponse updateQuantity(Long cartItemId, String email, CartItemDTORequest cartItemDTORequest) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()){
            throw new ResourceNotFoundException("Cart item was not found");
        }

        Cart cart = optionalCartItem.get().getCart();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        if (!cart.getUser().getId().equals(optionalUser.get().getId())){
            throw new AccessDeniedException( optionalUser.get().getFirstName() + " " + optionalUser.get().getLastName() + ", you cannot update another user's cart");
        }

        CartItem cartItem = optionalCartItem.get();
        cartItem.setQuantity(cartItemDTORequest.getQuantity());


        return cartItemMapper.toDTO(cartItem);
    }


}
