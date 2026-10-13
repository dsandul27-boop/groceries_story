package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // Satisfies JwtAuthenticationFilter's dependency so the @WebMvcTest context loads.
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void selfRegister_forcesCustomer_evenIfBodyAsksForAdmin() throws Exception {
        when(authService.register(any(User.class))).thenReturn(new UserDTOResponse());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"A","lastName":"B","email":"a@b.com",
                                 "password":"TestPass1!","phone":"060000000","role":"ADMIN"}
                                """))
                .andExpect(status().isCreated());

        // The role in the body was ADMIN; it must be forced to CUSTOMER before saving.
        verify(authService).register(captor.capture());
        assertEquals(Role.CUSTOMER, captor.getValue().getRole());
    }
}
