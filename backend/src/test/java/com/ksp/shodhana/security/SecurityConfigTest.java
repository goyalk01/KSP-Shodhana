package com.ksp.shodhana.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Verify JWT Token Generation and Verification")
    public void testJwtTokenGenerationAndValidation() {
        String token = jwtTokenProvider.generateToken("KSP-OFFICER-7892", "ROLE_INSPECTOR");

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("KSP-OFFICER-7892", jwtTokenProvider.getUsername(token));
        assertEquals("ROLE_INSPECTOR", jwtTokenProvider.getRole(token));
    }

    @Test
    @DisplayName("Verify Invalid Token Rejection")
    public void testInvalidTokenRejection() {
        assertFalse(jwtTokenProvider.validateToken("invalid.jwt.token.string"));
    }
}
