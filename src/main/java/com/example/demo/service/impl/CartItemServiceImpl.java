package com.example.demo.service.impl;

import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.dto.response.CartItemDTOResponse;
import com.example.demo.entities.Cart;
import com.example.demo.entities.CartItem;
import com.example.demo.entities.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartItemMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CartItemServiceImpl(CartItemMapper cartItemMapper, CartItemRepository cartItemRepository, ProductRepository productRepository, CartRepository cartRepository){
        this.cartItemMapper = cartItemMapper;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
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
        CartItemDTOResponse cartItemDTOResponse = cartItemMapper.toDTO(optionalCartItem.get());
        return cartItemDTOResponse;
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
            throw new ResourceNotFoundException("Cart already doesn't exist");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
