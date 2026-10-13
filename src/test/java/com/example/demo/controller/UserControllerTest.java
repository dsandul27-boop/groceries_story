package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.response.UserDTOResponse;
import com.example.demo.entities.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.MethodSecurityTestConfig.class)
class UserControllerTest {

    /**
     * Turns on method security so the controller's @PreAuthorize is enforced in
     * the slice. The HTTP chain is left to @WebMvcTest's default security
     * auto-config, which integrates with @WithMockUser.
     */
    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private static final String BODY = """
            {
              "firstName": "Test",
              "lastName": "User",
              "email": "created@example.com",
              "password": "TestPass1!",
              "phone": "060000000",
              "role": "STAFF"
            }
            """;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateUser() throws Exception {
        when(authService.register(any(User.class))).thenReturn(new UserDTOResponse());

        mockMvc.perform(post("/user/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotCreateUser() throws Exception {
        mockMvc.perform(post("/user/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }
}
