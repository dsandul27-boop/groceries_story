
package com.example.demo.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class JwtUtilTest {

    private static final String TEST_SECRET =
            "my-test-secret-key-must-be-at-least-32-characters";


    @Test
    public void generateAndValidateToken_shouldPreserveUsernameAndRole() {
        // Arrange
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET, 60_000);
        String username = "testuser";
        String role = "ADMIN";

        // Act
        String token = jwtUtil.generateToken(username, role);

        // Assert
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(role, jwtUtil.extractRole(token));
    }


    @Test
    public void expiredToken_shouldFailValidation() throws InterruptedException {
        // Arrange
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET, 1);

        // Act
        String token = jwtUtil.generateToken("testuser", "ADMIN");
        Thread.sleep(50);

        // Assert
        assertFalse(jwtUtil.validateToken(token));
    }


    @Test
    public void blankSecret_shouldThrowIllegalStateException() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtUtil("   ", 60_000)
        );
    }

}

