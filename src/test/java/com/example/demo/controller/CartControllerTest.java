package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(CartControllerTest.MethodSecurityTestConfig.class)
class CartControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartServiceImpl cartService;

    @MockitoBean
    private JwtUtil jwtUtil;

    // --- TESTS FOR: delete ---

    @Test
    @WithMockUser(username = "user", roles = "CUSTOMER")
    void customerCannotDeleteCart() throws Exception {
        mockMvc.perform(delete("/cart/delete-cart-by-id/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
        verify(cartService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteCart() throws Exception {
        mockMvc.perform(delete("/cart/delete-cart-by-id/1").with(csrf()))
                .andExpect(status().isNoContent());
        verify(cartService).delete(1L);
    }

    // --- TESTS FOR: getAll ---

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAll_staffOrAdmin_returnsOk() throws Exception {
        when(cartService.getAll("admin@example.com"))
                .thenReturn(List.of());
        mockMvc.perform(get("/cart/get-all-carts"))
                .andExpect(status().isOk());
        verify(cartService).getAll("admin@example.com");
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "CUSTOMER")
    void getAll_customer_returnsForbidden() throws Exception {
        mockMvc.perform(get("/cart/get-all-carts"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(cartService);
    }

    // --- TESTS FOR: getById (Secured to ADMIN now) ---

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getById_admin_returnsOk() throws Exception {
        when(cartService.getById(1L))
                .thenReturn(mock(CartDTOResponse.class));
        mockMvc.perform(get("/cart/get-cart-by-id/{cartId}", 1L))
                .andExpect(status().isOk());
        verify(cartService).getById(1L);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "CUSTOMER")
    void getById_customer_returnsForbidden() throws Exception {
        mockMvc.perform(get("/cart/get-cart-by-id/{cartId}", 1L))
                .andExpect(status().isForbidden());
        verifyNoInteractions(cartService);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getById_zeroId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/cart/get-cart-by-id/{cartId}", 0))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(cartService);
    }

    // --- TESTS FOR: getByEmail (CUSTOMER own-cart endpoint) ---

    @Test
    @WithMockUser(username = "user@example.com", roles = "CUSTOMER")
    void getByEmail_customer_returnsOk() throws Exception {
        when(cartService.getByEmail("user@example.com"))
                .thenReturn(mock(CartDTOResponse.class));

        // No params needed anymore since it fetches by authenticated context
        mockMvc.perform(get("/cart/get-by-user-email"))
                .andExpect(status().isOk());

        verify(cartService).getByEmail("user@example.com");
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getByEmail_admin_returnsForbidden() throws Exception {
        mockMvc.perform(get("/cart/get-by-user-email"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(cartService);
    }
}
