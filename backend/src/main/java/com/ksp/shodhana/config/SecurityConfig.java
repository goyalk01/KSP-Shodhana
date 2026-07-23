package com.ksp.shodhana.config;

import com.ksp.shodhana.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Enterprise Security Configuration & Role-Based Access Control (RBAC).
 */
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean isOfficerAuthorized(String token, String requiredRole) {
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }
        String role = jwtTokenProvider.getRole(token);
        return "ROLE_SUPERINTENDENT".equals(role) || requiredRole.equals(role) || "ROLE_OFFICER".equals(role);
    }
}
