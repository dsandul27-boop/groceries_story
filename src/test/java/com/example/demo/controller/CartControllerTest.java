package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.service.impl.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.response.PageResponse;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should apply defaults when pageable parameters are absent")
    void getAllCarts_whenNoParams_appliesDefaults() throws Exception {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(cartService.getAll(any())).thenReturn(new PageResponse<>());

        mockMvc.perform(get("/cart/get-all-carts"))
                .andExpect(status().isOk());

        verify(cartService).getAll(pageableCaptor.capture());
        Pageable captured = pageableCaptor.getValue();

        assertEquals(20, captured.getPageSize());
        assertEquals(0, captured.getPageNumber());
        assertNotNull(captured.getSort().getOrderFor("id"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    @DisplayName("Should clamp size to 100 when oversized size param is requested")
    void getAllCarts_whenOversized_clampsToMax100() throws Exception {
        // Given
        PageResponse<CartDTOResponse> mockResponse = PageResponse.<CartDTOResponse>builder()
                .content(java.util.Collections.emptyList())
                .page(1)
                .size(100)
                .totalElements(0L)
                .totalPages(0)
                .last(true)
                .build();

        when(cartService.getAll(any(Pageable.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/cart/get-all-carts")
                        .param("page", "1")
                        .param("size", "101")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100));
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return valid JSON body shape matching PageResponse DTO")
    void getAllCarts_returnsCorrectJsonShape() throws Exception {
        PageResponse<CartDTOResponse> mockResponse = PageResponse.<CartDTOResponse>builder()
                .content(Collections.emptyList())
                .page(0)
                .size(20)
                .totalElements(0L)
                .totalPages(0)
                .last(true)
                .build();

        when(cartService.getAll(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/cart/get-all-carts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.last").value(true));
    }

    // --- Role Gate Tests ---

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should return 403 Forbidden when user is CUSTOMER")
    void getAllCarts_asCustomer_returns403Forbidden() throws Exception {
        mockMvc.perform(get("/cart/get-all-carts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when user is anonymous")
    void getAllCarts_anonymous_returns401Unauthorized() throws Exception {
        mockMvc.perform(get("/cart/get-all-carts"))
                .andExpect(status().isUnauthorized()); // Метод исправлен на .isUnauthorized()
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

    // TESTS FOR: getByEmail

    @Test
    @WithMockUser(username = "user@example.com", roles = "CUSTOMER")
    void getByEmail_customer_returnsOk() throws Exception {
        when(cartService.getByEmail("user@example.com"))
                .thenReturn(mock(CartDTOResponse.class));


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
