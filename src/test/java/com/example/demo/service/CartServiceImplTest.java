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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.delete;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void getAll_returnsCartDTOList() {
        // Arrange
        Cart cart1 = mock(Cart.class);
        Cart cart2 = mock(Cart.class);

        CartDTOResponse dto1 = mock(CartDTOResponse.class);
        CartDTOResponse dto2 = mock(CartDTOResponse.class);

        when(cartRepository.findAll())
                .thenReturn(List.of(cart1, cart2));

        when(cartMapper.toDTO(cart1)).thenReturn(dto1);
        when(cartMapper.toDTO(cart2)).thenReturn(dto2);

        // Act
        List<CartDTOResponse> result = cartService.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);

        verify(cartRepository).findAll();
        verify(cartMapper).toDTO(cart1);
        verify(cartMapper).toDTO(cart2);
    }

    @Test
    void delete_deletesCartWithCorrectId() {
        // Arrange
        Long cartId = 10L;
        Long userId = 5L;
        String email = "customer@example.com";

        Cart cart = mock(Cart.class);
        User cartOwner = mock(User.class);
        User authenticatedUser = mock(User.class);

        when(cartRepository.findById(cartId))
                .thenReturn(Optional.of(cart));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(authenticatedUser));

        when(cart.getUser()).thenReturn(cartOwner);
        when(cartOwner.getId()).thenReturn(userId);
        when(authenticatedUser.getId()).thenReturn(userId);

        when(cartItemRepository.existsByCartId(cartId))
                .thenReturn(false);

        // Act
        cartService.delete(cartId);

        // Assert
        verify(cartRepository).deleteById(cartId);
        verify(cartRepository, never()).deleteById(20L);
    }

}
