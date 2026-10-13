
package com.example.demo.controller;

import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.config.JwtUtil;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)

class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;


    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateUser() throws Exception {

        when(authService.register(any(User.class)))
                .thenReturn(new UserDTOResponse());

        mockMvc.perform(post("/user/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Test",
                                  "lastName": "User",
                                  "email": "created@example.com",
                                  "password": "TestPass1!",
                                  "phone": "060000000",
                                  "role": "STAFF"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotCreateUser() throws Exception {
        mockMvc.perform(post("/user/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "firstName": "Test",
                          "lastName": "User",
                          "email": "created@example.com",
                          "password": "TestPass1!",
                          "phone": "060000000",
                          "role": "STAFF"
                        }
                        """))
                .andExpect(status().isForbidden());
    }

}
