package com.example.demo.service;

import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.dto.response.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import java.util.Collections;
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

    void getAll_withPopulatedPage_returnsCorrectPageResponse() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Cart cart = new Cart();
        CartDTOResponse dtoResponse = new CartDTOResponse();
        Page<Cart> cartPage = new PageImpl<>(List.of(cart), pageable, 1);

        when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        when(cartMapper.toResponse(cart)).thenReturn(dtoResponse);

        // When
        PageResponse<CartDTOResponse> result = cartService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());

        verify(cartRepository, times(1)).findAll(pageable);
        verify(cartMapper, times(1)).toResponse(cart);
    }

    @Test
    void getAll_withEmptyPage_returnsEmptyContentAndDoesNotThrow() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Cart> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(cartRepository.findAll(pageable)).thenReturn(emptyPage);

        // When & Then (No exception should be thrown)
        PageResponse<CartDTOResponse> result = assertDoesNotThrow(() -> cartService.getAll(pageable));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(cartRepository, times(1)).findAll(pageable);
        verifyNoInteractions(cartMapper);
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
