package com.example.demo.service;

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
import com.example.demo.service.impl.CartItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceImplTest {

    @Mock private CartItemMapper cartItemMapper;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private CartItemDTORequest request;
    private CartItemDTOResponse response;
    private final String email = "alice@x.com";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(email);

        cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        product = new Product();
        product.setId(20L);

        cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2L);

        request = new CartItemDTORequest();
        request.setProductId(20L);
        request.setQuantity(5L);

        response = new CartItemDTOResponse();
        response.setId(100L);
    }

    @Test
    void save_ExistingCartFound_ItemAdded() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(cart);
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDTO(any(CartItem.class))).thenReturn(response);

        CartItemDTOResponse result = cartItemService.save(request, email);

        assertNotNull(result);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void save_NoCart_NewCartCreated() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart); // BK-06 implicit-cart rule
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDTO(any(CartItem.class))).thenReturn(response);

        cartItemService.save(request, email);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(cartCaptor.capture());
        assertEquals(user, cartCaptor.getValue().getUser());
    }

    @Test
    void save_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartItemService.save(request, email));
    }

    @Test
    void save_ProductNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(cart);
        when(productRepository.findById(request.getProductId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartItemService.save(request, email));
    }

    @Test
    void updateQuantity_Owner_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(cart);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemMapper.toDTO(any(CartItem.class))).thenReturn(response);

        cartItemService.updateQuantity(100L, email, request);

        verify(cartItemRepository).save(cartItem);
        assertEquals(5L, cartItem.getQuantity());
    }

    @Test
    void updateQuantity_NonOwner_ThrowsAccessDeniedException() {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setEmail(email);

        Cart otherCart = new Cart();
        otherCart.setId(99L);
        otherCart.setUser(differentUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(differentUser));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(otherCart);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem)); // cartItem aparține de cart-ul 10L

        assertThrows(AccessDeniedException.class, () -> cartItemService.updateQuantity(100L, email, request));
    }

    @Test
    void updateQuantity_MissingItem_ThrowsResourceNotFoundException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(cart);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartItemService.updateQuantity(100L, email, request));
    }

    @Test
    void delete_Found_DeletesById() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(cart);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

        cartItemService.delete(100L, email);

        verify(cartItemRepository).deleteById(100L);
    }

    @Test
    void delete_Missing_ThrowsResourceNotFoundException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserEmail(email)).thenReturn(cart);
        when(cartItemRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartItemService.delete(100L, email));
    }
}
