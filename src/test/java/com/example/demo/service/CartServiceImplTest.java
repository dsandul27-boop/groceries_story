package com.example.demo.service;

import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.entities.Cart;
import com.example.demo.entities.User;
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
import org.springframework.security.access.AccessDeniedException;

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
    void getAll_returnsCallerCartDTOList() {
        String email = "user@example.com";

        User caller = mock(User.class);
        Cart cart = mock(Cart.class);
        CartDTOResponse dto = mock(CartDTOResponse.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(caller));

        when(caller.getEmail()).thenReturn(email);

        when(cartRepository.findCartByUserEmail(email))
                .thenReturn(cart);

        when(cartMapper.toDTO(cart))
                .thenReturn(dto);

        List<CartDTOResponse> result = cartService.getAll(email);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(userRepository).findByEmail(email);
        verify(cartRepository).findCartByUserEmail(email);
        verify(cartMapper).toDTO(cart);

        verify(cartRepository, never()).findAll();
    }

    @Test
    void delete_deletesCartWithCorrectId() {
        Long cartId = 10L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(mock(Cart.class)));
        when(cartItemRepository.existsByCartId(cartId)).thenReturn(false);

        cartService.delete(cartId);

        verify(cartRepository).deleteById(cartId);
    }

    @Test
    void getById_foreignCart_throwsAccessDenied() {
        Cart cart = mock(Cart.class); User owner = mock(User.class); User caller = mock(User.class);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(userRepository.findByEmail("alice@x.com")).thenReturn(Optional.of(caller));
        when(cart.getUser()).thenReturn(owner);
        when(owner.getId()).thenReturn(2L);
        when(caller.getId()).thenReturn(1L);

        assertThrows(AccessDeniedException.class, () -> cartService.getById(1L, "alice@x.com"));
    }

    @Test
    void getById_ownCart_returnsDto() {
        Cart cart = mock(Cart.class); User user = mock(User.class); CartDTOResponse dto = mock(CartDTOResponse.class);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(userRepository.findByEmail("alice@x.com")).thenReturn(Optional.of(user));
        when(cart.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(cartMapper.toDTO(cart)).thenReturn(dto);

        assertSame(dto, cartService.getById(1L, "alice@x.com"));
    }

}
