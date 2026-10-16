package com.example.demo.service;

import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.Cart;
import com.example.demo.entities.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.mapper.CartMapper;
import com.example.demo.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;


    @Test
    void delete_deletesCartWithCorrectId() {
        Long cartId = 10L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(mock(Cart.class)));
        when(cartItemRepository.existsByCartId(cartId)).thenReturn(false);

        cartService.delete(cartId);

        verify(cartRepository).deleteById(cartId);
    }





    @Test
    void getByEmail_existingUserWithCart_returnsDto() {
        String email = "user@example.com";

        User user = mock(User.class);
        Cart cart = mock(Cart.class);
        CartDTOResponse dto = mock(CartDTOResponse.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getEmail()).thenReturn(email);
        when(cartRepository.findCartByUserEmail(email))
                .thenReturn(cart);
        when(cartMapper.toDTO(cart)).thenReturn(dto);

        CartDTOResponse result = cartService.getByEmail(email);

        assertSame(dto, result);

        verify(userRepository).findByEmail(email);
        verify(cartRepository).findCartByUserEmail(email);
        verify(cartMapper).toDTO(cart);
    }



    @Test
    void getByEmail_userNotFound_throwsException() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getByEmail(email)
        );

        verify(cartRepository, never()).findCartByUserEmail(anyString());
        verify(cartMapper, never()).toDTO(any(Cart.class));
    }


    @Test
    void getByEmail_cartNotFound_throwsException() {
        String email = "user@example.com";

        User user = mock(User.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(user.getEmail()).thenReturn(email);
        when(cartRepository.findCartByUserEmail(email))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getByEmail(email)
        );

        verify(cartMapper, never()).toDTO(any(Cart.class));
    }


    @Test
    void getAll_multipleCarts_returnsAllDtos() {
        Cart cart1 = mock(Cart.class);
        Cart cart2 = mock(Cart.class);

        CartDTOResponse dto1 = mock(CartDTOResponse.class);
        CartDTOResponse dto2 = mock(CartDTOResponse.class);

        when(cartRepository.findAll())
                .thenReturn(List.of(cart1, cart2));
        when(cartMapper.toDTO(cart1)).thenReturn(dto1);
        when(cartMapper.toDTO(cart2)).thenReturn(dto2);

        List<CartDTOResponse> result =
                cartService.getAll("admin@example.com");

        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        verify(cartRepository).findAll();
        verify(cartMapper).toDTO(cart1);
        verify(cartMapper).toDTO(cart2);
    }


    @Test
    void getAll_noCarts_throwsException() {
        when(cartRepository.findAll()).thenReturn(List.of());

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getAll("admin@example.com")
        );

        verify(cartMapper, never()).toDTO(any(Cart.class));
    }


    @Test
    void getById_existingCart_returnsDto() {
        Long cartId = 1L;

        Cart cart = mock(Cart.class);
        CartDTOResponse dto = mock(CartDTOResponse.class);

        when(cartRepository.findById(cartId))
                .thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(dto);

        CartDTOResponse result = cartService.getById(cartId);

        assertSame(dto, result);

        verify(cartRepository).findById(cartId);
        verify(cartMapper).toDTO(cart);
    }


}
