
package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.response.CartDTOResponse;
import com.example.demo.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCanReadCart() throws Exception {
        String email = "user";
        when(cartService.getById(1L, email))
                .thenReturn(new CartDTOResponse());

        mockMvc.perform(get("/cart/get-cart-by-id/1"))
                .andExpect(status().isOk());

        verify(cartService).getById(1L, email);
    }

    @Test
    @WithMockUser(username = "user",roles = "CUSTOMER")
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


    @Test
    void anonymousCannotReadCart() throws Exception {
        var result = mockMvc.perform(get("/cart/get-cart-by-id/1"))
                .andReturn();

        int statusCode = result.getResponse().getStatus();

        assertTrue(
                statusCode == 401 || statusCode == 403,
                "Expected 401 or 403, but received " + statusCode
        );

        verifyNoInteractions(cartService);
    }

    @Test
    @WithMockUser(username = "alice@x.com", roles = "CUSTOMER")
    void foreignCart_403() throws Exception {
        when(cartService.getById(99L, "alice@x.com"))
                .thenThrow(new AccessDeniedException("nope"));

        mockMvc.perform(get("/cart/get-cart-by-id/99"))
                .andExpect(status().isForbidden());
    }

}
