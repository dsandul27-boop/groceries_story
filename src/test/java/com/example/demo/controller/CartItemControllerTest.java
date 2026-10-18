package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.request.CartItemDTORequest;
import com.example.demo.service.CartItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)

@Import(CartControllerTest.MethodSecurityTestConfig.class)
public class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CartItemService cartItemService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "alice@x.com", roles = "CUSTOMER")
    void getById_CustomerAuthorized_Returns200AndForwardsEmail() throws Exception {
        mockMvc.perform(get("/cart-item/get-cart-item-by-id/100"))
                .andExpect(status().isOk());
        verify(cartItemService).getById(100L, "alice@x.com");
    }

    @Test
    @WithMockUser(username = "staff@x.com", roles = "STAFF")
    void getById_StaffForbidden_Returns403() throws Exception {
        mockMvc.perform(get("/cart-item/get-cart-item-by-id/100"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(cartItemService);
    }

    @Test
    void getAll_Anonymous_Returns401() throws Exception {
        mockMvc.perform(get("/cart-item/get-all-cart-items"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice@x.com", roles = "CUSTOMER")
    void save_CustomerAuthorized_Returns201AndForwards() throws Exception {
        CartItemDTORequest request = new CartItemDTORequest();
        request.setProductId(1L);
        request.setQuantity(2L);
        mockMvc.perform(post("/cart-item/save-cart-item")

                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        verify(cartItemService).save(any(CartItemDTORequest.class), eq("alice@x.com"));
    }

    @Test
    @WithMockUser(username = "staff@x.com", roles = "STAFF")
    void save_StaffForbidden_Returns403() throws Exception {
        mockMvc.perform(post("/cart-item/save-cart-item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "productId": 1,
                                "quantity": 2
                            }
                            """))
                .andExpect(status().isForbidden());
    }

    @Test
    void save_Anonymous_Returns401() throws Exception {
        mockMvc.perform(post("/cart-item/save-cart-item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "productId": 1,
                                "quantity": 2
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice@x.com", roles = "CUSTOMER")
    void updateQuantity_CustomerAuthorized_Returns200AndForwards() throws Exception {
        CartItemDTORequest request = new CartItemDTORequest();
        request.setProductId(1L);
        request.setQuantity(3L);
        mockMvc.perform(patch("/cart-item/update-cart-item/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(cartItemService).updateQuantity(eq(100L), eq("alice@x.com"), any(CartItemDTORequest.class));
    }

    @Test
    @WithMockUser(username = "staff@x.com", roles = "STAFF")
    void updateQuantity_StaffForbidden_Returns403() throws Exception {
        mockMvc.perform(patch("/cart-item/update-cart-item/100")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "productId": 1,
                                "quantity": 3
                            }
                            """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice@x.com", roles = "CUSTOMER")
    void delete_CustomerAuthorized_Returns204AndForwards() throws Exception {
        mockMvc.perform(delete("/cart-item/delete-cart-item/100").with(csrf()))
                .andExpect(status().isNoContent());
        verify(cartItemService).delete(100L, "alice@x.com");
    }

    @Test
    @WithMockUser(username = "staff@x.com", roles = "STAFF")
    void delete_StaffForbidden_Returns403() throws Exception {
        mockMvc.perform(delete("/cart-item/delete-cart-item/100").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice@x.com", roles = "CUSTOMER")
    void getAll_CustomerAuthorized_Returns200AndForwardsEmail() throws Exception {
        mockMvc.perform(get("/cart-item/get-all-cart-items"))
                .andExpect(status().isOk());
        verify(cartItemService).getAll("alice@x.com");
    }

    @Test
    @WithMockUser(username = "staff@x.com", roles = "STAFF")
    void getAll_StaffForbidden_Returns403() throws Exception {
        mockMvc.perform(get("/cart-item/get-all-cart-items"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(cartItemService);
    }
}
