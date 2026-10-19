package com.example.demo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityChainIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private JwtUtil jwtUtil;

    private static final String PROTECTED_ENDPOINT = "/api/test-secure";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void protectedEndpoint_withMissingToken_returns401Unauthorized() throws Exception {
        mockMvc.perform(get(PROTECTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void protectedEndpoint_withInvalidOrExpiredToken_returns401ViaEntryPoint() throws Exception {
        String badToken = "invalid.expired.jwt.token";
        when(jwtUtil.validateToken(badToken)).thenReturn(false);

        mockMvc.perform(get(PROTECTED_ENDPOINT)
                        .header("Authorization", "Bearer " + badToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource."));
    }

    @Test
    void protectedEndpoint_withValidToken_bypassesFiltersButReturns404InsteadOf401() throws Exception {

        String validToken = "valid.test.token";
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("test@example.com");
        when(jwtUtil.extractRole(validToken)).thenReturn("CUSTOMER");

        mockMvc.perform(get(PROTECTED_ENDPOINT)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
