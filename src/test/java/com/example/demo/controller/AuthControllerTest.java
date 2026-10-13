package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.User;
import com.example.demo.entities.Role; // Убедитесь, что импорт вашего Enum Role правильный
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Чтобы контекст безопасности успешно собрался

    @Test
    void selfRegister_forcesCustomer_evenIfBodyAsksForAdmin() throws Exception {
        when(authService.register(any(User.class))).thenReturn(new UserDTOResponse());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"firstName":"A","lastName":"B","email":"a@b.com",
                     "password":"TestPass1!","phone":"060000000","role":"ADMIN"}
                    """))
                .andExpect(status().isCreated());

        // Перехватываем объект, который ушел в сервис, и проверяем, что роль подменилась на CUSTOMER
        verify(authService).register(captor.capture());
        assertEquals(Role.CUSTOMER, captor.getValue().getRole());
    }
}
